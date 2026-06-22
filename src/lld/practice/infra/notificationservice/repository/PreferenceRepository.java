package lld.practice.infra.notificationservice.repository;

import lld.practice.infra.notificationservice.enums.Channel;
import lld.practice.infra.notificationservice.enums.NotificationCategory;
import lld.practice.infra.notificationservice.model.UserPreference;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PreferenceRepository {
    private final ConcurrentMap<String, UserPreference> store = new ConcurrentHashMap<>();

    public void save(UserPreference pref) {
        store.put(key(pref.getUserId(), pref.getCategory(), pref.getChannel()), pref);
    }

    public Optional<UserPreference> find(String userId, NotificationCategory category, Channel channel) {
        return Optional.ofNullable(store.get(key(userId, category, channel)));
    }

    private String key(String userId, NotificationCategory cat, Channel ch) {
        return userId + ":" + cat + ":" + ch;
    }
}
