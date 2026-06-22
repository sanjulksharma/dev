package lld.practice.splitswise.expense.split;

import lld.practice.splitswise.expense.ResolvedShare;
import lld.practice.splitswise.expense.Share;

import java.util.List;

/**
 * Strategy pattern — encapsulates one algorithm for splitting a total amount
 * across participants.
 *
 * <p>Adding a new way to split is just a new implementation + factory registration;
 * existing strategies and callers don't change. (Open/Closed Principle.)
 *
 * <p>Implementations MUST:
 * <ul>
 *   <li>{@link #validate(double, List)} eagerly so that bad input never makes it past the gate.</li>
 *   <li>Ensure {@code Σ ResolvedShare.amount == totalAmount} (rounding remainder
 *       assigned deterministically — typically to the payer or the first participant).</li>
 * </ul>
 */
public interface SplitStrategy {

    /** Validate the user-supplied shares against this strategy's rules. */
    void validate(double totalAmount, List<Share> shares);

    /** Convert raw shares into concrete amounts. The sum equals {@code totalAmount}. */
    List<ResolvedShare> resolve(double totalAmount, List<Share> shares);
}
