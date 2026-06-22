package lld.practice.infra.notificationservice.service;

import lld.practice.infra.notificationservice.dto.NotificationRequest;
import lld.practice.infra.notificationservice.dto.NotificationResponse;
import lld.practice.infra.notificationservice.enums.Channel;
import lld.practice.infra.notificationservice.enums.NotificationStatus;
import lld.practice.infra.notificationservice.exception.NotificationException;
import lld.practice.infra.notificationservice.model.Notification;
import lld.practice.infra.notificationservice.queue.NotificationQueue;
import lld.practice.infra.notificationservice.queue.QueueMessage;
import lld.practice.infra.notificationservice.repository.NotificationRepository;
import lld.practice.infra.notificationservice.repository.UserRepository;
import lld.practice.infra.notificationservice.util.IdGenerator;

/**
 * Validates the request, persists the envelope, dedupes, and enqueues per-channel work.
 */
public class IntakeService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationQueue queue;

    public IntakeService(NotificationRepository nr, UserRepository ur, NotificationQueue q) {
        this.notificationRepository = nr;
        this.userRepository = ur;
        this.queue = q;
    }

    public NotificationResponse submit(NotificationRequest req) {
        validate(req);

        if (req.getDedupeKey() != null) {
            var existing = notificationRepository.findByDedupeKey(req.getTenantId(), req.getDedupeKey());
            if (existing.isPresent()) {
                Notification n = existing.get();
                return new NotificationResponse(n.getId(), n.getStatus(), "Duplicate dedupe_key, returning existing.");
            }
        }

        Notification n = new Notification(
                IdGenerator.newId("notif"),
                req.getTenantId(),
                req.getUserId(),
                req.getTypeCode(),
                req.getCategory(),
                req.getChannels(),
                req.getPayload(),
                req.getPriority(),
                req.getDedupeKey(),
                req.getScheduledAt()
        );
        n.setStatus(NotificationStatus.QUEUED);
        notificationRepository.save(n);

        for (Channel ch : req.getChannels()) {
            queue.enqueue(new QueueMessage(n.getId(), ch, 1), req.getPriority());
        }

        return new NotificationResponse(n.getId(), NotificationStatus.QUEUED, "Accepted");
    }

    private void validate(NotificationRequest req) {
        if (req.getTenantId() == null) throw new NotificationException("tenantId required");
        if (req.getUserId() == null) throw new NotificationException("userId required");
        if (req.getTypeCode() == null) throw new NotificationException("typeCode required");
        if (req.getCategory() == null) throw new NotificationException("category required");
        if (req.getChannels() == null || req.getChannels().isEmpty())
            throw new NotificationException("at least one channel required");
        if (userRepository.findById(req.getUserId()).isEmpty())
            throw new NotificationException("Unknown userId: " + req.getUserId());
    }
}
