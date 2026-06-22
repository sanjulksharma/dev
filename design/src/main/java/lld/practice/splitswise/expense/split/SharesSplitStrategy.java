package lld.practice.splitswise.expense.split;

import lld.practice.splitswise.exception.InvalidSplitException;
import lld.practice.splitswise.expense.ResolvedShare;
import lld.practice.splitswise.expense.Share;

import java.util.ArrayList;
import java.util.List;

/**
 * Shares-based split — e.g., A:2, B:1, C:1 splits ₹400 into 200/100/100.
 * Useful for things like "I'll take a double room, you take singles".
 */
public class SharesSplitStrategy implements SplitStrategy {

    @Override
    public void validate(double totalAmount, List<Share> shares) {
        if (shares == null || shares.isEmpty()) {
            throw new InvalidSplitException("SHARES split requires at least one participant");
        }
        double sumShares = shares.stream().mapToDouble(Share::getValue).sum();
        if (sumShares <= 0) {
            throw new InvalidSplitException("SHARES split requires positive shares, got " + sumShares);
        }
    }

    @Override
    public List<ResolvedShare> resolve(double totalAmount, List<Share> shares) {
        long totalCents = Math.round(totalAmount * 100);
        double totalShares = shares.stream().mapToDouble(Share::getValue).sum();
        List<ResolvedShare> result = new ArrayList<>(shares.size());
        long allocated = 0;
        for (int i = 0; i < shares.size(); i++) {
            Share s = shares.get(i);
            long cents = Math.round(totalCents * (s.getValue() / totalShares));
            if (i == shares.size() - 1) {
                cents = totalCents - allocated;
            }
            allocated += cents;
            result.add(new ResolvedShare(s.getUserId(), cents / 100.0));
        }
        return result;
    }
}
