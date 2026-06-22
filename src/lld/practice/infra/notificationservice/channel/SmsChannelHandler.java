package lld.practice.infra.notificationservice.channel;

import lld.practice.infra.notificationservice.enums.Channel;
import lld.practice.infra.notificationservice.model.Notification;
import lld.practice.infra.notificationservice.model.User;
import lld.practice.infra.notificationservice.util.IdGenerator;

public class SmsChannelHandler extends AbstractChannelHandler {

    private static final int SMS_BODY_MAX = 1600;

    @Override
    public Channel getChannel() { return Channel.SMS; }

    @Override
    protected boolean validate(User user, Notification notification, RenderedMessage message) {
        return user.getPhone() != null && !user.getPhone().isEmpty()
                && message.getBody() != null
                && message.getBody().length() <= SMS_BODY_MAX;
    }

    @Override
    protected ChannelResult deliver(User user, Notification notification, RenderedMessage message) {
        // Real impl: Twilio SDK call
        System.out.printf("[SMS] to=%s | body=%s%n", user.getPhone(), message.getBody());
        return ChannelResult.ok(IdGenerator.newId("twilio"));
    }
}
