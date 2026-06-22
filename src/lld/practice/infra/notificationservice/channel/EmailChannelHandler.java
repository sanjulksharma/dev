package lld.practice.infra.notificationservice.channel;

import lld.practice.infra.notificationservice.enums.Channel;
import lld.practice.infra.notificationservice.model.Notification;
import lld.practice.infra.notificationservice.model.User;
import lld.practice.infra.notificationservice.util.IdGenerator;

public class EmailChannelHandler extends AbstractChannelHandler {

    @Override
    public Channel getChannel() { return Channel.EMAIL; }

    @Override
    protected boolean validate(User user, Notification notification, RenderedMessage message) {
        return user.getEmail() != null && !user.getEmail().isEmpty()
                && message.getSubject() != null && !message.getSubject().isEmpty();
    }

    @Override
    protected ChannelResult deliver(User user, Notification notification, RenderedMessage message) {
        // Real impl: SES/SendGrid SDK call
        System.out.printf("[EMAIL] to=%s | subj=%s | body=%s%n",
                user.getEmail(), message.getSubject(), message.getBody());
        return ChannelResult.ok(IdGenerator.newId("ses"));
    }
}
