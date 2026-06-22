package lld.practice.infra.notificationservice.service;

import lld.practice.infra.notificationservice.enums.Channel;
import lld.practice.infra.notificationservice.enums.NotificationStatus;
import lld.practice.infra.notificationservice.model.DeliveryAttempt;
import lld.practice.infra.notificationservice.repository.DeliveryAttemptRepository;
import lld.practice.infra.notificationservice.util.IdGenerator;

import java.util.List;

public class DeliveryTrackerService {

    private final DeliveryAttemptRepository repo;

    public DeliveryTrackerService(DeliveryAttemptRepository repo) {
        this.repo = repo;
    }

    public DeliveryAttempt recordAttempt(String notificationId, Channel channel, int attemptNo,
                                         NotificationStatus state, String providerMsgId, String errorCode) {
        DeliveryAttempt attempt = new DeliveryAttempt(
                IdGenerator.newId("att"), notificationId, channel, attemptNo, state);
        attempt.setProviderMsgId(providerMsgId);
        attempt.setErrorCode(errorCode);
        repo.save(attempt);
        return attempt;
    }

    public List<DeliveryAttempt> getAttempts(String notificationId) {
        return repo.findByNotificationId(notificationId);
    }
}
