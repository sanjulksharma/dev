package lld.practice.infra.notificationservice.model;

import lld.practice.infra.notificationservice.enums.Channel;
import lld.practice.infra.notificationservice.enums.NotificationStatus;

import java.time.Instant;

public class DeliveryAttempt {
    private final String id;
    private final String notificationId;
    private final Channel channel;
    private final int attemptNo;
    private NotificationStatus state;
    private String providerMsgId;
    private String errorCode;
    private final Instant attemptedAt;

    public DeliveryAttempt(String id, String notificationId, Channel channel,
                           int attemptNo, NotificationStatus state) {
        this.id = id;
        this.notificationId = notificationId;
        this.channel = channel;
        this.attemptNo = attemptNo;
        this.state = state;
        this.attemptedAt = Instant.now();
    }

    public String getId() { return id; }
    public String getNotificationId() { return notificationId; }
    public Channel getChannel() { return channel; }
    public int getAttemptNo() { return attemptNo; }
    public NotificationStatus getState() { return state; }
    public String getProviderMsgId() { return providerMsgId; }
    public String getErrorCode() { return errorCode; }
    public Instant getAttemptedAt() { return attemptedAt; }

    public void setState(NotificationStatus state) { this.state = state; }
    public void setProviderMsgId(String id) { this.providerMsgId = id; }
    public void setErrorCode(String code) { this.errorCode = code; }

    @Override
    public String toString() {
        return String.format("DeliveryAttempt{notif=%s, channel=%s, attempt=%d, state=%s, providerMsg=%s, err=%s}",
                notificationId, channel, attemptNo, state, providerMsgId, errorCode);
    }
}
