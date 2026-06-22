package lld.practice.infra.notificationservice.channel;

import lld.practice.infra.notificationservice.enums.Channel;
import lld.practice.infra.notificationservice.model.Device;
import lld.practice.infra.notificationservice.model.Notification;
import lld.practice.infra.notificationservice.model.User;
import lld.practice.infra.notificationservice.util.IdGenerator;

public class PushChannelHandler extends AbstractChannelHandler {

    @Override
    public Channel getChannel() { return Channel.PUSH; }

    @Override
    protected boolean validate(User user, Notification notification, RenderedMessage message) {
        return user.getDevices() != null && !user.getDevices().isEmpty();
    }

    @Override
    protected ChannelResult deliver(User user, Notification notification, RenderedMessage message) {
        // Real impl: FCM (Android/Web) and APNs (iOS) calls
        for (Device device : user.getDevices()) {
            System.out.printf("[PUSH:%s] token=%s | title=%s | body=%s%n",
                    device.getPlatform(), device.getPushToken(),
                    message.getSubject(), message.getBody());
        }
        return ChannelResult.ok(IdGenerator.newId("fcm"));
    }
}
