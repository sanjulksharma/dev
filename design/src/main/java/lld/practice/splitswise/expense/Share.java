package lld.practice.splitswise.expense;

import java.util.Objects;

/**
 * Raw, user-supplied input describing one participant's share of an expense.
 *
 * <p>The semantics of {@code value} depend on the split strategy:
 * <ul>
 *   <li>EQUAL    — value is ignored</li>
 *   <li>EXACT    — value is the absolute amount</li>
 *   <li>PERCENT  — value is a percentage (0..100)</li>
 *   <li>SHARES   — value is the number of shares (e.g., 2 vs 1)</li>
 *   <li>ADJUST   — value is a positive/negative tweak applied on top of an equal split</li>
 * </ul>
 *
 * <p>Kept immutable to make splits safe to reuse across threads and easy to reason about.
 */
public final class Share {
    private final String userId;
    private final double value;

    public Share(String userId, double value) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.value = value;
    }

    public String getUserId() { return userId; }
    public double getValue() { return value; }
}
