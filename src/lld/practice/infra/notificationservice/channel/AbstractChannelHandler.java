package lld.practice.infra.notificationservice.channel;

import lld.practice.infra.notificationservice.model.Notification;
import lld.practice.infra.notificationservice.model.User;

/**
 * Template Method: subclasses implement validate + deliver; base wires the flow.
 */
public abstract class AbstractChannelHandler implements ChannelHandler {

    @Override
    public final ChannelResult send(User user, Notification notification, RenderedMessage message) {
        try {
            if (!validate(user, notification, message)) {
                return ChannelResult.fail("VALIDATION_FAILED");
            }
            return deliver(user, notification, message);
        } catch (Exception e) {
            return ChannelResult.fail("EXCEPTION:" + e.getClass().getSimpleName());
        }
    }

    protected abstract boolean validate(User user, Notification notification, RenderedMessage message);
    protected abstract ChannelResult deliver(User user, Notification notification, RenderedMessage message);
}
