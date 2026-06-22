package lld.practice.fintech.splitswise.expense.split;

import lld.practice.fintech.splitswise.exception.InvalidSplitException;
import lld.practice.fintech.splitswise.expense.ResolvedShare;
import lld.practice.fintech.splitswise.expense.Share;

import java.util.ArrayList;
import java.util.List;

/**
 * Percentages must add up to 100. Resolution is done in integer cents to avoid
 * floating drift, and any 1-cent remainder is assigned to the first participant.
 */
public class PercentageSplitStrategy implements SplitStrategy {

    private static final double EPSILON = 0.01;

    @Override
    public void validate(double totalAmount, List<Share> shares) {
        if (shares == null || shares.isEmpty()) {
            throw new InvalidSplitException("PERCENTAGE split requires at least one participant");
        }
        double sum = shares.stream().mapToDouble(Share::getValue).sum();
        if (Math.abs(sum - 100.0) > EPSILON) {
            throw new InvalidSplitException("PERCENTAGE shares must sum to 100, got " + sum);
        }
    }

    @Override
    public List<ResolvedShare> resolve(double totalAmount, List<Share> shares) {
        long totalCents = Math.round(totalAmount * 100);
        List<ResolvedShare> result = new ArrayList<>(shares.size());
        long allocated = 0;
        for (int i = 0; i < shares.size(); i++) {
            Share s = shares.get(i);
            long cents = Math.round(totalCents * (s.getValue() / 100.0));
            if (i == shares.size() - 1) {
                cents = totalCents - allocated;
            }
            allocated += cents;
            result.add(new ResolvedShare(s.getUserId(), cents / 100.0));
        }
        return result;
    }
}
