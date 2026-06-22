package lld.practice.splitswise.expense;

import java.util.Objects;

/**
 * Output of a {@code SplitStrategy}: the concrete amount one user owes for an expense.
 *
 * <p>Immutable by design — once the strategy has produced a resolved share,
 * the value is what the system records and what balances are derived from.
 */
public final class ResolvedShare {
    private final String userId;
    private final double amount;

    public ResolvedShare(String userId, double amount) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.amount = amount;
    }

    public String getUserId() { return userId; }
    public double getAmount() { return amount; }
}
