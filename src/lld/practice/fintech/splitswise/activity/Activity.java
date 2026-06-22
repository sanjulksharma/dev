package lld.practice.fintech.splitswise.activity;

import lld.practice.fintech.splitswise.enums.Currency;

import java.util.Objects;
import java.util.UUID;

/**
 * Append-only audit record describing one action against an expense or settlement.
 *
 * <p>Constructed via {@link Builder} (Builder pattern) — there are too many
 * optional/correlated fields for a constructor to remain readable.
 */
public class Activity {
    private final String activityId;
    private final String userId;
    private final String expenseId;
    private final double amount;
    private final String operation;
    private final Currency currency;
    private final long timestampMillis;

    private Activity(Builder b) {
        this.activityId = b.activityId != null ? b.activityId : UUID.randomUUID().toString();
        this.userId = b.userId;
        this.expenseId = b.expenseId;
        this.amount = b.amount;
        this.operation = b.operation;
        this.currency = b.currency;
        this.timestampMillis = b.timestampMillis == 0 ? System.currentTimeMillis() : b.timestampMillis;
    }

    public String getActivityId() { return activityId; }
    public String getUserId() { return userId; }
    public String getExpenseId() { return expenseId; }
    public double getAmount() { return amount; }
    public String getOperation() { return operation; }
    public Currency getCurrency() { return currency; }
    public long getTimestampMillis() { return timestampMillis; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String activityId;
        private String userId;
        private String expenseId;
        private double amount;
        private String operation;
        private Currency currency;
        private long timestampMillis;

        public Builder activityId(String v) { this.activityId = v; return this; }
        public Builder userId(String v) { this.userId = v; return this; }
        public Builder expenseId(String v) { this.expenseId = v; return this; }
        public Builder amount(double v) { this.amount = v; return this; }
        public Builder operation(String v) { this.operation = v; return this; }
        public Builder currency(Currency v) { this.currency = v; return this; }
        public Builder timestampMillis(long v) { this.timestampMillis = v; return this; }

        public Activity build() {
            Objects.requireNonNull(operation, "operation");
            return new Activity(this);
        }
    }
}
