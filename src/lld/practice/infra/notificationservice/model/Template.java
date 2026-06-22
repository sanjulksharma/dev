package lld.practice.infra.notificationservice.model;

import lld.practice.infra.notificationservice.enums.Channel;

public class Template {
    private final String id;
    private final String typeCode;
    private final Channel channel;
    private final String locale;
    private final int version;
    private final String subject;
    private final String body;

    public Template(String id, String typeCode, Channel channel, String locale,
                    int version, String subject, String body) {
        this.id = id;
        this.typeCode = typeCode;
        this.channel = channel;
        this.locale = locale;
        this.version = version;
        this.subject = subject;
        this.body = body;
    }

    public String getId() { return id; }
    public String getTypeCode() { return typeCode; }
    public Channel getChannel() { return channel; }
    public String getLocale() { return locale; }
    public int getVersion() { return version; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
}
