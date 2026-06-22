package lld.practice.infra.notificationservice.channel;

public class ChannelResult {
    private final boolean success;
    private final String providerMsgId;
    private final String errorCode;

    private ChannelResult(boolean success, String providerMsgId, String errorCode) {
        this.success = success;
        this.providerMsgId = providerMsgId;
        this.errorCode = errorCode;
    }

    public static ChannelResult ok(String providerMsgId) {
        return new ChannelResult(true, providerMsgId, null);
    }

    public static ChannelResult fail(String errorCode) {
        return new ChannelResult(false, null, errorCode);
    }

    public boolean isSuccess() { return success; }
    public String getProviderMsgId() { return providerMsgId; }
    public String getErrorCode() { return errorCode; }
}
