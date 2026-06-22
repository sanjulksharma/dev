package lld.practice.infra.notificationservice.model;

public class Device {
    public enum Platform { IOS, ANDROID, WEB }

    private final String id;
    private final String userId;
    private final Platform platform;
    private final String pushToken;

    public Device(String id, String userId, Platform platform, String pushToken) {
        this.id = id;
        this.userId = userId;
        this.platform = platform;
        this.pushToken = pushToken;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public Platform getPlatform() { return platform; }
    public String getPushToken() { return pushToken; }
}
