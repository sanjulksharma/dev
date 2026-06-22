package lld.practice.fintech.splitswise.expense.split;

import lld.practice.fintech.splitswise.exception.InvalidSplitException;
import lld.practice.fintech.splitswise.expense.ResolvedShare;
import lld.practice.fintech.splitswise.expense.Share;

import java.util.ArrayList;
import java.util.List;

/**
 * Equal split plus per-user adjustment. Common pattern: "split the dinner
 * equally, but I had an extra dessert worth +₹50".
 *
 * <p>For each participant: {@code owe = (total - Σ adjustments)/n + adjustment[i]}.
 */
public class AdjustmentSplitStrategy implements SplitStrategy {

    @Override
    public void validate(double totalAmount, List<Share> shares) {
        if (shares == null || shares.isEmpty()) {
            throw new InvalidSplitException("ADJUSTMENT split requires at least one participant");
        }
        double sumAdjustments = shares.stream().mapToDouble(Share::getValue).sum();
        if (sumAdjustments > totalAmount) {
            throw new InvalidSplitException(
                    "ADJUSTMENT sum exceeds total: " + sumAdjustments + " > " + totalAmount);
        }
    }

    @Override
    public List<ResolvedShare> resolve(double totalAmount, List<Share> shares) {
        long totalCents = Math.round(totalAmount * 100);
        long adjCentsSum = shares.stream().mapToLong(s -> Math.round(s.getValue() * 100)).sum();
        long remaining = totalCents - adjCentsSum;
        long base = remaining / shares.size();
        long roundingRemainder = remaining - (base * shares.size());

        List<ResolvedShare> result = new ArrayList<>(shares.size());
        for (int i = 0; i < shares.size(); i++) {
            Share s = shares.get(i);
            long adjCents = Math.round(s.getValue() * 100);
            long cents = base + adjCents + (i == 0 ? roundingRemainder : 0);
            result.add(new ResolvedShare(s.getUserId(), cents / 100.0));
        }
        return result;
    }
}
