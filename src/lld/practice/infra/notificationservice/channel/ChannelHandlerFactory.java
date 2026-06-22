package lld.practice.infra.notificationservice.channel;

import lld.practice.infra.notificationservice.enums.Channel;
import lld.practice.infra.notificationservice.exception.NotificationException;

import java.util.EnumMap;
import java.util.Map;

/**
 * Factory + Registry. Holds singleton handler instances per channel.
 */
public class ChannelHandlerFactory {
    private final Map<Channel, ChannelHandler> handlers = new EnumMap<>(Channel.class);

    public ChannelHandlerFactory() {
        register(new EmailChannelHandler());
        register(new SmsChannelHandler());
        register(new PushChannelHandler());
        register(new InAppChannelHandler());
    }

    public void register(ChannelHandler handler) {
        handlers.put(handler.getChannel(), handler);
    }

    public ChannelHandler get(Channel channel) {
        ChannelHandler h = handlers.get(channel);
        if (h == null) {
            throw new NotificationException("No handler registered for channel: " + channel);
        }
        return h;
    }
}
