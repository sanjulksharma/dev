package lld.practice.infra.notificationservice.service;

import lld.practice.infra.notificationservice.channel.ChannelHandler;
import lld.practice.infra.notificationservice.channel.ChannelHandlerFactory;
import lld.practice.infra.notificationservice.channel.ChannelResult;
import lld.practice.infra.notificationservice.channel.RenderedMessage;
import lld.practice.infra.notificationservice.enums.NotificationStatus;
import lld.practice.infra.notificationservice.enums.Priority;
import lld.practice.infra.notificationservice.model.Notification;
import lld.practice.infra.notificationservice.model.User;
import lld.practice.infra.notificationservice.queue.NotificationQueue;
import lld.practice.infra.notificationservice.queue.QueueMessage;
import lld.practice.infra.notificationservice.repository.NotificationRepository;
import lld.practice.infra.notificationservice.repository.UserRepository;

/**
 * Pulls a queue message, resolves user + preferences + template, calls the channel handler,
 * records the delivery attempt, and re-enqueues on transient failures (with cap).
 */
public class DispatchService {

    private static final int MAX_ATTEMPTS = 3;

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final PreferenceService preferenceService;
    private final TemplateService templateService;
    private final ChannelHandlerFactory channelHandlerFactory;
    private final RateLimiter rateLimiter;
    private final DeliveryTrackerService trackerService;
    private final NotificationQueue queue;

    public DispatchService(NotificationRepository nr,
                           UserRepository ur,
                           PreferenceService ps,
                           TemplateService ts,
                           ChannelHandlerFactory chf,
                           RateLimiter rl,
                           DeliveryTrackerService dts,
                           NotificationQueue queue) {
        this.notificationRepository = nr;
        this.userRepository = ur;
        this.preferenceService = ps;
        this.templateService = ts;
        this.channelHandlerFactory = chf;
        this.rateLimiter = rl;
        this.trackerService = dts;
        this.queue = queue;
    }

    public void dispatch(QueueMessage msg) {
        Notification notification = notificationRepository.findById(msg.getNotificationId()).orElse(null);
        if (notification == null) return;

        User user = userRepository.findById(notification.getUserId()).orElse(null);
        if (user == null) {
            trackerService.recordAttempt(notification.getId(), msg.getChannel(),
                    msg.getAttemptNo(), NotificationStatus.FAILED, null, "USER_NOT_FOUND");
            return;
        }

        if (!preferenceService.canSend(user, msg.getChannel(), notification.getCategory(), notification.getPriority())) {
            trackerService.recordAttempt(notification.getId(), msg.getChannel(),
                    msg.getAttemptNo(), NotificationStatus.SUPPRESSED, null, "PREF_BLOCKED");
            return;
        }

        if (!rateLimiter.allow(user.getId(), msg.getChannel())) {
            trackerService.recordAttempt(notification.getId(), msg.getChannel(),
                    msg.getAttemptNo(), NotificationStatus.SUPPRESSED, null, "RATE_LIMITED");
            return;
        }

        RenderedMessage rendered;
        try {
            rendered = templateService.render(notification.getTypeCode(), msg.getChannel(),
                    user.getLocale(), notification.getPayload());
        } catch (Exception e) {
            trackerService.recordAttempt(notification.getId(), msg.getChannel(),
                    msg.getAttemptNo(), NotificationStatus.FAILED, null, "RENDER_FAILED");
            return;
        }

        ChannelHandler handler = channelHandlerFactory.get(msg.getChannel());
        ChannelResult result = handler.send(user, notification, rendered);

        if (result.isSuccess()) {
            trackerService.recordAttempt(notification.getId(), msg.getChannel(),
                    msg.getAttemptNo(), NotificationStatus.SENT, result.getProviderMsgId(), null);
            notification.setStatus(NotificationStatus.SENT);
        } else {
            trackerService.recordAttempt(notification.getId(), msg.getChannel(),
                    msg.getAttemptNo(), NotificationStatus.FAILED, null, result.getErrorCode());

            if (msg.getAttemptNo() < MAX_ATTEMPTS && isTransient(result.getErrorCode())) {
                queue.enqueue(
                        new QueueMessage(notification.getId(), msg.getChannel(), msg.getAttemptNo() + 1),
                        retryPriority(notification.getPriority())
                );
            } else {
                notification.setStatus(NotificationStatus.FAILED);
            }
        }
    }

    private boolean isTransient(String errorCode) {
        if (errorCode == null) return false;
        return errorCode.startsWith("EXCEPTION") || errorCode.contains("TIMEOUT") || errorCode.contains("5");
    }

    private Priority retryPriority(Priority original) {
        Priority[] p = Priority.values();
        int next = Math.min(original.ordinal() + 1, p.length - 1);
        return p[next];
    }
}
