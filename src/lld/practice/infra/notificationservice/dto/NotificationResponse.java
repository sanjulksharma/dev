package lld.practice.infra.notificationservice.dto;

import lld.practice.infra.notificationservice.enums.NotificationStatus;

public class NotificationResponse {
    private final String notificationId;
    private final NotificationStatus status;
    private final String message;

    public NotificationResponse(String notificationId, NotificationStatus status, String message) {
        this.notificationId = notificationId;
        this.status = status;
        this.message = message;
    }

    public String getNotificationId() { return notificationId; }
    public NotificationStatus getStatus() { return status; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return String.format("NotificationResponse{id=%s, status=%s, msg=%s}",
                notificationId, status, message);
    }
}
