package lld.practice.infra.notificationservice.service;

import lld.practice.infra.notificationservice.enums.Channel;
import lld.practice.infra.notificationservice.enums.NotificationCategory;
import lld.practice.infra.notificationservice.enums.Priority;
import lld.practice.infra.notificationservice.model.User;
import lld.practice.infra.notificationservice.model.UserPreference;
import lld.practice.infra.notificationservice.repository.PreferenceRepository;

import java.time.LocalTime;
import java.time.ZonedDateTime;

public class PreferenceService {

    private final PreferenceRepository preferenceRepository;

    public PreferenceService(PreferenceRepository repo) {
        this.preferenceRepository = repo;
    }

    public void savePreference(UserPreference pref) {
        preferenceRepository.save(pref);
    }

    /**
     * Decide whether the user can be reached now on this channel.
     * Transactional + P0 messages bypass quiet hours but still respect explicit opt-out.
     */
    public boolean canSend(User user, Channel channel, NotificationCategory category, Priority priority) {
        UserPreference pref = preferenceRepository.find(user.getId(), category, channel).orElse(null);

        if (pref != null && !pref.isEnabled()) {
            return false;
        }

        boolean criticalOverride = category == NotificationCategory.TRANSACTIONAL
                || priority == Priority.P0;

        if (pref != null && !criticalOverride) {
            LocalTime nowLocal = ZonedDateTime.now(user.getTimezone()).toLocalTime();
            if (pref.isInQuietHours(nowLocal)) return false;
        }
        return true;
    }
}
