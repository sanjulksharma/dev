package lld.practice.infra.notificationservice.model;

import lld.practice.infra.notificationservice.enums.Channel;
import lld.practice.infra.notificationservice.enums.NotificationCategory;
import lld.practice.infra.notificationservice.enums.NotificationStatus;
import lld.practice.infra.notificationservice.enums.Priority;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Notification {
    private final String id;
    private final String tenantId;
    private final String userId;
    private final String typeCode;
    private final NotificationCategory category;
    private final Set<Channel> requestedChannels;
    private final Map<String, Object> payload;
    private final Priority priority;
    private final String dedupeKey;
    private final Instant scheduledAt;
    private final Instant createdAt;
    private NotificationStatus status;

    public Notification(String id, String tenantId, String userId, String typeCode,
                        NotificationCategory category, Set<Channel> requestedChannels,
                        Map<String, Object> payload, Priority priority,
                        String dedupeKey, Instant scheduledAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.userId = userId;
        this.typeCode = typeCode;
        this.category = category;
        this.requestedChannels = requestedChannels != null ? requestedChannels : new HashSet<>();
        this.payload = payload != null ? payload : new HashMap<>();
        this.priority = priority;
        this.dedupeKey = dedupeKey;
        this.scheduledAt = scheduledAt;
        this.createdAt = Instant.now();
        this.status = NotificationStatus.PENDING;
    }

    public String getId() { return id; }
    public String getTenantId() { return tenantId; }
    public String getUserId() { return userId; }
    public String getTypeCode() { return typeCode; }
    public NotificationCategory getCategory() { return category; }
    public Set<Channel> getRequestedChannels() { return requestedChannels; }
    public Map<String, Object> getPayload() { return payload; }
    public Priority getPriority() { return priority; }
    public String getDedupeKey() { return dedupeKey; }
    public Instant getScheduledAt() { return scheduledAt; }
    public Instant getCreatedAt() { return createdAt; }
    public NotificationStatus getStatus() { return status; }

    public void setStatus(NotificationStatus status) { this.status = status; }
}
