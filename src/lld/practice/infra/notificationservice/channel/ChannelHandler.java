package lld.practice.infra.notificationservice.channel;

import lld.practice.infra.notificationservice.enums.Channel;
import lld.practice.infra.notificationservice.model.Notification;
import lld.practice.infra.notificationservice.model.User;

public interface ChannelHandler {
    Channel getChannel();
    ChannelResult send(User user, Notification notification, RenderedMessage message);
}
