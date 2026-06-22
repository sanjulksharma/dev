package lld.practice.splitswise.settlement;

import lld.practice.splitswise.enums.Currency;
import lld.practice.splitswise.user.User;

import java.util.Objects;
import java.util.UUID;

/**
 * Record of a cash payment from {@code payer} to {@code payee}. Settles a debt
 * (in part or in full); reduces the corresponding balance entries.
 *
 * <p>Immutable — once recorded, a settlement is an audit fact. To reverse one,
 * record another settlement in the opposite direction.
 */
public final class Settlement {
    private final String settlementId;
    private final String groupId;
    private final User payer;
    private final User payee;
    private final double amount;
    private final Currency currency;
    private final String note;
    private final long settledAtMillis;

    public Settlement(String groupId, User payer, User payee, double amount, Currency currency, String note) {
        if (payer.equals(payee)) {
            throw new IllegalArgumentException("payer and payee cannot be the same user");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }
        this.settlementId = UUID.randomUUID().toString();
        this.groupId = groupId;
        this.payer = Objects.requireNonNull(payer);
        this.payee = Objects.requireNonNull(payee);
        this.amount = amount;
        this.currency = Objects.requireNonNull(currency);
        this.note = note;
        this.settledAtMillis = System.currentTimeMillis();
    }

    public String getSettlementId() { return settlementId; }
    public String getGroupId() { return groupId; }
    public User getPayer() { return payer; }
    public User getPayee() { return payee; }
    public double getAmount() { return amount; }
    public Currency getCurrency() { return currency; }
    public String getNote() { return note; }
    public long getSettledAtMillis() { return settledAtMillis; }
}
