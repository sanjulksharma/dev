package lld.practice.splitswise.expense.split;

import lld.practice.splitswise.exception.InvalidSplitException;
import lld.practice.splitswise.expense.ResolvedShare;
import lld.practice.splitswise.expense.Share;

import java.util.ArrayList;
import java.util.List;

/**
 * Splits the total equally among all participants. Rounding remainder
 * (e.g., 100/3 = 33.33 + 33.33 + 33.34) is assigned to the first participant
 * to keep the sum exact.
 */
public class EqualSplitStrategy implements SplitStrategy {

    @Override
    public void validate(double totalAmount, List<Share> shares) {
        if (shares == null || shares.isEmpty()) {
            throw new InvalidSplitException("EQUAL split requires at least one participant");
        }
    }

    @Override
    public List<ResolvedShare> resolve(double totalAmount, List<Share> shares) {
        long totalCents = Math.round(totalAmount * 100);
        long per = totalCents / shares.size();
        long remainder = totalCents - (per * shares.size());

        List<ResolvedShare> result = new ArrayList<>(shares.size());
        for (int i = 0; i < shares.size(); i++) {
            long cents = per + (i == 0 ? remainder : 0);
            result.add(new ResolvedShare(shares.get(i).getUserId(), cents / 100.0));
        }
        return result;
    }
}
