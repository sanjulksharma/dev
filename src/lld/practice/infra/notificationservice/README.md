# Notification Service — Low-Level Design (Java)

A multi-channel notification system: email, SMS, push, in-app. Async, priority-laned, idempotent, with retries, rate limiting, user preferences, and template rendering.

---

## 1. Functional Requirements

- **Multi-channel delivery**: Email, SMS, Push (iOS/Android), In-App, Webhook, Slack.
- **Notification types**: Transactional (OTP, receipts), Promotional (offers), System (alerts), Digest.
- **User preferences**: Opt-in/out per channel × type; quiet hours; locale.
- **Templating**: Versioned templates with variable substitution and i18n.
- **Scheduling**: Immediate, delayed (deliver at T), recurring (cron).
- **Bulk / fan-out**: Send to a segment (e.g., 10M users) from one request.
- **Retries** with exponential backoff on transient channel failures.
- **Delivery tracking**: queued → sent → delivered → opened/clicked → failed.
- **Idempotency** via client-supplied `dedupe_key`.
- **Rate limiting** per user, per channel, per tenant.
- **Priority lanes** (P0 transactional preempts P3 marketing).

## 2. Non-Functional Requirements

| Concern        | Target                                              |
|----------------|-----------------------------------------------------|
| Throughput     | 100K notif/sec peak, 10K sustained                  |
| Latency (P99)  | < 500 ms intake → channel handoff for transactional |
| Availability   | 99.99% for intake API                               |
| Durability     | Zero loss after 200 OK (WAL + replicated queue)     |
| Consistency    | Strong for preferences write; eventual for status   |
| Scalability    | Horizontal — stateless services, partitioned queues |
| Security       | mTLS internal, PII encrypted at rest, signed webhooks |
| Observability  | Per-message trace, per-channel dashboards, DLQ alerts |

---

## 3. Entities (Data Model)

```
User(id, tenant_id, locale, timezone, created_at)
Device(id, user_id, platform, push_token, last_seen_at)            // for push
Channel = enum{EMAIL, SMS, PUSH, IN_APP, WEBHOOK, SLACK}
NotificationType(code, category, default_priority)                 // e.g., "ORDER_SHIPPED"
Template(id, type_code, channel, locale, version, subject, body)
UserPreference(user_id, category, channel, enabled, quiet_hours, frequency_cap)

Notification(                                                      // the request envelope
  id, tenant_id, user_id, type_code, dedupe_key,
  payload_json, priority, scheduled_at, status, created_at
)

DeliveryAttempt(
  id, notification_id, channel, attempt_no,
  state{QUEUED,SENT,DELIVERED,OPENED,FAILED,SUPPRESSED},
  provider_msg_id, error_code, attempted_at
)

Campaign(id, segment_query, template_id, schedule, status)         // bulk
SuppressionList(user_id, channel, reason, until_at)                // bounces, unsubscribes
```

**Indexes**: `Notification(user_id, created_at)`, `DeliveryAttempt(notification_id)`,
unique `(tenant_id, dedupe_key)`.

### Entity Class Mapping (Java)

| Entity            | Class                                                  |
|-------------------|--------------------------------------------------------|
| User              | `lld.practice.infra.notificationservice.model.User`                          |
| Device            | `lld.practice.infra.notificationservice.model.Device`                        |
| Channel           | `lld.practice.infra.notificationservice.enums.Channel`                       |
| Priority          | `lld.practice.infra.notificationservice.enums.Priority`                      |
| NotificationStatus| `lld.practice.infra.notificationservice.enums.NotificationStatus`            |
| NotificationCategory | `lld.practice.infra.notificationservice.enums.NotificationCategory`       |
| Template          | `lld.practice.infra.notificationservice.model.Template`                      |
| UserPreference    | `lld.practice.infra.notificationservice.model.UserPreference`                |
| Notification      | `lld.practice.infra.notificationservice.model.Notification`                  |
| DeliveryAttempt   | `lld.practice.infra.notificationservice.model.DeliveryAttempt`               |
| RenderedMessage   | `lld.practice.infra.notificationservice.channel.RenderedMessage`             |

---

## 4. APIs (REST sketch — implemented as `NotificationFacade`)

**Send**
- `POST /notifications` — `{user_id, type, channels?, payload, dedupe_key, scheduled_at?, priority?}` → `202 {id}`
- `POST /notifications/bulk` — `{segment_id|user_ids[], type, payload}` → `202 {campaign_id}`
- `GET  /notifications/{id}` — envelope + latest delivery state per channel
- `GET  /users/{id}/notifications?cursor=` — inbox (in-app)

**Preferences**
- `GET  /users/{id}/preferences`
- `PUT  /users/{id}/preferences` — bulk upsert
- `POST /users/{id}/unsubscribe?token=...` — public unsubscribe link

**Devices**
- `POST /users/{id}/devices` — register push token
- `DELETE /users/{id}/devices/{deviceId}`

**Templates** (admin)
- `POST /templates`, `PUT /templates/{id}`, `GET /templates?type=&channel=&locale=`

**Webhooks (provider callbacks)**
- `POST /callbacks/{provider}` — delivery/bounce/open events (signature-verified)

---

## 5. Services & Flow

```
                      ┌────────────┐
Client ──HTTPS──▶  API Gateway  ──▶  Notification Intake Svc  ──▶  Queue: notif.in
                      └────────────┘     │ validate, idempotency, persist
                                         ▼
                                   Postgres (Notification)
                                         │
            ┌────────────────────────────┼────────────────────────────┐
            ▼                            ▼                            ▼
    Preference Svc              Template Svc                  Scheduler Svc
    (Redis cache + DB)         (versioned, cached)        (delayed/cron jobs → notif.in)
                                         │
                                         ▼
                            Routing/Dispatcher Workers
                            (consume notif.in, fan out per channel)
                                         │
            ┌────────────┬───────────────┼───────────────┬───────────────┐
            ▼            ▼               ▼               ▼               ▼
       Email Worker   SMS Worker     Push Worker     InApp Worker    Webhook Worker
       (SES/SendGrid) (Twilio)       (FCM/APNs)      (DB + WS push)  (HTTP+HMAC)
            │            │               │               │               │
            └─────────── all emit ─────▶ Queue: notif.events ◀──────────┘
                                         │
                                         ▼
                              Delivery Tracker Svc → DeliveryAttempt
                              Analytics Svc       → OLAP store
```

### Service Responsibilities

| Service             | Java class                                          | Purpose                                                       |
|---------------------|-----------------------------------------------------|---------------------------------------------------------------|
| Intake              | `IntakeService`                                     | AuthN/Z, validate, idempotency check, persist, enqueue        |
| Preference          | `PreferenceService`                                 | Channel eligibility, quiet hours, suppression                 |
| Template            | `TemplateService`                                   | Render `{type, channel, locale}` → subject/body               |
| Scheduler           | `SchedulerService`                                  | Delayed delivery (Redis ZSET / Kafka delay topic in prod)     |
| Dispatcher          | `DispatchService` + `DispatchWorker`                | Pull queue, fan out to channels, retry on transient failure   |
| Channel Handlers    | `EmailChannelHandler`, `SmsChannelHandler`, `PushChannelHandler`, `InAppChannelHandler` | Per-provider client, validation, delivery |
| Rate Limiter        | `RateLimiter`                                       | Token-bucket per `user:channel`                               |
| Delivery Tracker    | `DeliveryTrackerService`                            | Records `DeliveryAttempt` lifecycle                           |
| Facade              | `NotificationFacade`                                | Singleton entry point that wires everything                   |

---

## 6. Key Design Choices

- **Async by default**: API returns 202 after durable write; queue guarantees at-least-once.
- **Idempotency**: Unique `(tenant_id, dedupe_key)` index prevents dupes on retry.
- **Priority lanes**: Separate queues `notif.in.p0` … `notif.in.p3`; workers weighted-pull.
- **Backpressure & isolation**: One slow provider can't starve others — per-channel worker pools and circuit breakers.
- **Bulk pipeline**: Campaign service expands a segment lazily into the queue in batches.
- **Quiet hours**: Evaluated at dispatch time using user TZ; reschedule rather than drop. Transactional/P0 bypass.
- **Security**: Webhook callbacks verified via HMAC; PII columns encrypted (KMS); unsubscribe tokens are signed JWTs.

---

## 7. Package Layout

```
lld.practice.infra.notificationservice
├── enums         // Channel, Priority, NotificationStatus, NotificationCategory
├── model         // User, Device, Notification, Template, UserPreference, DeliveryAttempt
├── dto           // NotificationRequest, NotificationResponse
├── repository    // In-memory stores (replace with JPA/Mongo in prod)
├── channel       // Channel handlers (Email, SMS, Push, InApp) — Strategy + Template Method
├── queue         // NotificationQueue (priority lanes) + DispatchWorker
├── service       // Intake, Preference, Template, Dispatch, RateLimiter, DeliveryTracker, Scheduler
├── api           // NotificationFacade (Singleton)
├── exception     // NotificationException, RateLimitExceededException, TemplateNotFoundException
└── util          // IdGenerator, TemplateRenderer
```

## 8. Patterns Used

- **Strategy** — `ChannelHandler` per channel
- **Template Method** — `AbstractChannelHandler.send()` wires validate → deliver
- **Factory / Registry** — `ChannelHandlerFactory`
- **Singleton** — `NotificationFacade`
- **Producer-Consumer** — `NotificationQueue` + `DispatchWorker` pool
- **Builder** — `NotificationRequest.builder()`

---

## 9. Run

```bash
cd src/main/java
find . -name "*.java" | xargs javac -d /tmp/notif-build
java -cp /tmp/notif-build lld.practice.infra.notificationservice.NotificationMain
```

Or open the folder in any IDE and run `lld.practice.infra.notificationservice.NotificationMain`.

### Demo scenarios in `Main.java`

1. Register user + device + templates + preferences.
2. Send a **transactional** P0 notification across EMAIL/SMS/PUSH/IN_APP — all delivered.
3. Resubmit the same `dedupe_key` — returns the existing notification ID (idempotency).
4. Send a **promotional** P3 notification on EMAIL + SMS — SMS suppressed (`PREF_BLOCKED`) because the user opted out of promo SMS.
5. Print the per-channel `DeliveryAttempt` history for both notifications.
