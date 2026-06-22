package lld.practice.splitswise.user;

import lld.practice.splitswise.enums.Currency;

import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a user of the system.
 *
 * <p>Equality is intentionally based on the immutable {@code userId} only —
 * mutable profile fields (name/email) must not influence collection lookups.
 */
public class User {
    private final String userId;
    private String name;
    private String email;
    private String phone;
    private Currency defaultCurrency;

    public User(String email, String name) {
        this(UUID.randomUUID().toString(), email, name, null, Currency.INR);
    }

    public User(String userId, String email, String name, String phone, Currency defaultCurrency) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.defaultCurrency = defaultCurrency;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Currency getDefaultCurrency() { return defaultCurrency; }
    public void setDefaultCurrency(Currency defaultCurrency) { this.defaultCurrency = defaultCurrency; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return Objects.equals(userId, ((User) o).userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
