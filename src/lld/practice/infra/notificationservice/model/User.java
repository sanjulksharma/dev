package lld.practice.infra.notificationservice.model;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class User {
    private final String id;
    private final String name;
    private final String email;
    private final String phone;
    private final String locale;
    private final ZoneId timezone;
    private final List<Device> devices = new ArrayList<>();

    public User(String id, String name, String email, String phone, String locale, ZoneId timezone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.locale = locale;
        this.timezone = timezone;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getLocale() { return locale; }
    public ZoneId getTimezone() { return timezone; }
    public List<Device> getDevices() { return devices; }

    public void addDevice(Device device) { devices.add(device); }
}
