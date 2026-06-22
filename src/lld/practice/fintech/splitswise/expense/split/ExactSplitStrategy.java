package lld.practice.fintech.splitswise.expense.split;

import lld.practice.fintech.splitswise.exception.InvalidSplitException;
import lld.practice.fintech.splitswise.expense.ResolvedShare;
import lld.practice.fintech.splitswise.expense.Share;

import java.util.ArrayList;
import java.util.List;

/**
 * The user enters the exact amount each participant owes. We just validate the
 * sum and pass through.
 */
public class ExactSplitStrategy implements SplitStrategy {

    private static final double EPSILON = 0.01;

    @Override
    public void validate(double totalAmount, List<Share> shares) {
        if (shares == null || shares.isEmpty()) {
            throw new InvalidSplitException("EXACT split requires at least one participant");
        }
        double sum = shares.stream().mapToDouble(Share::getValue).sum();
        if (Math.abs(sum - totalAmount) > EPSILON) {
            throw new InvalidSplitException(
                    "EXACT shares sum to " + sum + " but expense total is " + totalAmount);
        }
    }

    @Override
    public List<ResolvedShare> resolve(double totalAmount, List<Share> shares) {
        List<ResolvedShare> result = new ArrayList<>(shares.size());
        for (Share s : shares) {
            result.add(new ResolvedShare(s.getUserId(), s.getValue()));
        }
        return result;
    }
}
