package lld.practice.infra.notificationservice.repository;

import lld.practice.infra.notificationservice.model.Notification;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class NotificationRepository {
    private final ConcurrentMap<String, Notification> byId = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, String> byDedupeKey = new ConcurrentHashMap<>();

    public void save(Notification n) {
        byId.put(n.getId(), n);
        if (n.getDedupeKey() != null) {
            byDedupeKey.putIfAbsent(dedupeFullKey(n.getTenantId(), n.getDedupeKey()), n.getId());
        }
    }

    public Optional<Notification> findById(String id) { return Optional.ofNullable(byId.get(id)); }

    public Optional<Notification> findByDedupeKey(String tenantId, String dedupeKey) {
        if (dedupeKey == null) return Optional.empty();
        String existingId = byDedupeKey.get(dedupeFullKey(tenantId, dedupeKey));
        return existingId == null ? Optional.empty() : findById(existingId);
    }

    private String dedupeFullKey(String tenantId, String dedupeKey) {
        return tenantId + "::" + dedupeKey;
    }
}
