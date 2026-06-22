package lld.practice.infra.notificationservice.dto;

import lld.practice.infra.notificationservice.enums.Channel;
import lld.practice.infra.notificationservice.enums.NotificationCategory;
import lld.practice.infra.notificationservice.enums.Priority;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NotificationRequest {
    private String tenantId;
    private String userId;
    private String typeCode;
    private NotificationCategory category;
    private Set<Channel> channels = new HashSet<>();
    private Map<String, Object> payload = new HashMap<>();
    private Priority priority = Priority.P2;
    private String dedupeKey;
    private Instant scheduledAt;

    public static Builder builder() { return new Builder(); }

    public String getTenantId() { return tenantId; }
    public String getUserId() { return userId; }
    public String getTypeCode() { return typeCode; }
    public NotificationCategory getCategory() { return category; }
    public Set<Channel> getChannels() { return channels; }
    public Map<String, Object> getPayload() { return payload; }
    public Priority getPriority() { return priority; }
    public String getDedupeKey() { return dedupeKey; }
    public Instant getScheduledAt() { return scheduledAt; }

    public static class Builder {
        private final NotificationRequest req = new NotificationRequest();

        public Builder tenantId(String t) { req.tenantId = t; return this; }
        public Builder userId(String u) { req.userId = u; return this; }
        public Builder typeCode(String c) { req.typeCode = c; return this; }
        public Builder category(NotificationCategory c) { req.category = c; return this; }
        public Builder channels(Set<Channel> c) { req.channels = c; return this; }
        public Builder addChannel(Channel c) { req.channels.add(c); return this; }
        public Builder payload(Map<String, Object> p) { req.payload = p; return this; }
        public Builder addPayload(String key, Object value) { req.payload.put(key, value); return this; }
        public Builder priority(Priority p) { req.priority = p; return this; }
        public Builder dedupeKey(String k) { req.dedupeKey = k; return this; }
        public Builder scheduledAt(Instant t) { req.scheduledAt = t; return this; }

        public NotificationRequest build() { return req; }
    }
}
