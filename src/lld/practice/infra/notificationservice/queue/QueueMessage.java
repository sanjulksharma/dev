package lld.practice.infra.notificationservice.queue;

import lld.practice.infra.notificationservice.enums.Channel;

/**
 * One unit of work flowing through the queue: a notification to send on a specific channel.
 */
public class QueueMessage {
    private final String notificationId;
    private final Channel channel;
    private final int attemptNo;

    public QueueMessage(String notificationId, Channel channel, int attemptNo) {
        this.notificationId = notificationId;
        this.channel = channel;
        this.attemptNo = attemptNo;
    }

    public String getNotificationId() { return notificationId; }
    public Channel getChannel() { return channel; }
    public int getAttemptNo() { return attemptNo; }
}
