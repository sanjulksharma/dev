package lld.practice.infra.notificationservice.repository;

import lld.practice.infra.notificationservice.model.DeliveryAttempt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DeliveryAttemptRepository {
    private final ConcurrentMap<String, List<DeliveryAttempt>> byNotification = new ConcurrentHashMap<>();

    public void save(DeliveryAttempt attempt) {
        byNotification.computeIfAbsent(attempt.getNotificationId(), k ->
                Collections.synchronizedList(new ArrayList<>())).add(attempt);
    }

    public List<DeliveryAttempt> findByNotificationId(String notificationId) {
        return byNotification.getOrDefault(notificationId, Collections.emptyList());
    }
}
