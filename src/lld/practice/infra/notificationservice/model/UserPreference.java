package lld.practice.infra.notificationservice.model;

import lld.practice.infra.notificationservice.enums.Channel;
import lld.practice.infra.notificationservice.enums.NotificationCategory;

import java.time.LocalTime;

public class UserPreference {
    private final String userId;
    private final NotificationCategory category;
    private final Channel channel;
    private boolean enabled;
    private LocalTime quietHoursStart;
    private LocalTime quietHoursEnd;
    private int frequencyCapPerDay;

    public UserPreference(String userId, NotificationCategory category, Channel channel, boolean enabled) {
        this.userId = userId;
        this.category = category;
        this.channel = channel;
        this.enabled = enabled;
        this.frequencyCapPerDay = Integer.MAX_VALUE;
    }

    public String getUserId() { return userId; }
    public NotificationCategory getCategory() { return category; }
    public Channel getChannel() { return channel; }
    public boolean isEnabled() { return enabled; }
    public LocalTime getQuietHoursStart() { return quietHoursStart; }
    public LocalTime getQuietHoursEnd() { return quietHoursEnd; }
    public int getFrequencyCapPerDay() { return frequencyCapPerDay; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setQuietHours(LocalTime start, LocalTime end) {
        this.quietHoursStart = start;
        this.quietHoursEnd = end;
    }
    public void setFrequencyCapPerDay(int cap) { this.frequencyCapPerDay = cap; }

    public boolean isInQuietHours(LocalTime now) {
        if (quietHoursStart == null || quietHoursEnd == null) return false;
        if (quietHoursStart.isBefore(quietHoursEnd)) {
            return !now.isBefore(quietHoursStart) && now.isBefore(quietHoursEnd);
        }
        return !now.isBefore(quietHoursStart) || now.isBefore(quietHoursEnd);
    }
}
