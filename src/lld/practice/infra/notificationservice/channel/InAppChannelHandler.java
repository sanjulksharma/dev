package lld.practice.infra.notificationservice.channel;

import lld.practice.infra.notificationservice.enums.Channel;
import lld.practice.infra.notificationservice.model.Notification;
import lld.practice.infra.notificationservice.model.User;
import lld.practice.infra.notificationservice.util.IdGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Persists notifications to an in-memory inbox keyed by userId.
 * In prod: write to a DB and push via WebSocket to connected clients.
 */
public class InAppChannelHandler extends AbstractChannelHandler {

    private final Map<String, List<RenderedMessage>> inbox = new ConcurrentHashMap<>();

    @Override
    public Channel getChannel() { return Channel.IN_APP; }

    @Override
    protected boolean validate(User user, Notification notification, RenderedMessage message) {
        return user.getId() != null && message.getBody() != null;
    }

    @Override
    protected ChannelResult deliver(User user, Notification notification, RenderedMessage message) {
        inbox.computeIfAbsent(user.getId(), k -> new ArrayList<>()).add(message);
        System.out.printf("[IN_APP] user=%s | subj=%s | body=%s%n",
                user.getId(), message.getSubject(), message.getBody());
        return ChannelResult.ok(IdGenerator.newId("inapp"));
    }

    public List<RenderedMessage> getInbox(String userId) {
        return inbox.getOrDefault(userId, new ArrayList<>());
    }
}
