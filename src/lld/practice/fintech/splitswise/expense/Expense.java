package lld.practice.fintech.splitswise.expense;

import lld.practice.fintech.splitswise.enums.Currency;
import lld.practice.fintech.splitswise.enums.ExpenseType;
import lld.practice.fintech.splitswise.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Domain entity: an expense within (optionally) a group.
 *
 * <p>Constructed through {@link Builder} (Builder pattern) — the constructor has
 * too many parameters and several optional ones (description/category/groupId),
 * which is exactly the case Builder is designed for. The expense is immutable
 * except for {@code resolvedShares} which is set once by the service after the
 * split strategy runs.
 */
public class Expense {
    private final String expenseId;
    private final ExpenseType expenseType;
    private final double amount;
    private final User paidBy;
    private final User createdBy;
    private final String groupId;
    private final Currency currency;
    private final String description;
    private final List<Share> shares;
    private List<ResolvedShare> resolvedShares;

    private Expense(Builder b) {
        this.expenseId = b.expenseId != null ? b.expenseId : UUID.randomUUID().toString();
        this.expenseType = b.expenseType;
        this.amount = b.amount;
        this.paidBy = b.paidBy;
        this.createdBy = b.createdBy;
        this.groupId = b.groupId;
        this.currency = b.currency;
        this.description = b.description;
        this.shares = b.shares != null ? new ArrayList<>(b.shares) : new ArrayList<>();
    }

    public String getExpenseId() { return expenseId; }
    public ExpenseType getExpenseType() { return expenseType; }
    public double getAmount() { return amount; }
    public User getPaidBy() { return paidBy; }
    public User getCreatedBy() { return createdBy; }
    public String getGroupId() { return groupId; }
    public Currency getCurrency() { return currency; }
    public String getDescription() { return description; }
    public List<Share> getShares() { return Collections.unmodifiableList(shares); }
    public List<ResolvedShare> getResolvedShares() {
        return resolvedShares == null ? Collections.emptyList() : Collections.unmodifiableList(resolvedShares);
    }

    /** Called exactly once by the expense service after the split strategy resolves shares. */
    public void setResolvedShares(List<ResolvedShare> resolvedShares) {
        this.resolvedShares = new ArrayList<>(resolvedShares);
    }

    public static Builder builder() { return new Builder(); }

    /** Fluent Builder — keeps construction readable and lets us add fields without breaking callers. */
    public static class Builder {
        private String expenseId;
        private ExpenseType expenseType;
        private double amount;
        private User paidBy;
        private User createdBy;
        private String groupId;
        private Currency currency;
        private String description;
        private List<Share> shares;

        public Builder expenseId(String v) { this.expenseId = v; return this; }
        public Builder expenseType(ExpenseType v) { this.expenseType = v; return this; }
        public Builder amount(double v) { this.amount = v; return this; }
        public Builder paidBy(User v) { this.paidBy = v; return this; }
        public Builder createdBy(User v) { this.createdBy = v; return this; }
        public Builder groupId(String v) { this.groupId = v; return this; }
        public Builder currency(Currency v) { this.currency = v; return this; }
        public Builder description(String v) { this.description = v; return this; }
        public Builder shares(List<Share> v) { this.shares = v; return this; }

        public Expense build() {
            if (expenseType == null) throw new IllegalStateException("expenseType is required");
            if (paidBy == null)      throw new IllegalStateException("paidBy is required");
            if (createdBy == null)   throw new IllegalStateException("createdBy is required");
            if (currency == null)    throw new IllegalStateException("currency is required");
            if (amount <= 0)         throw new IllegalStateException("amount must be > 0");
            return new Expense(this);
        }
    }
}
