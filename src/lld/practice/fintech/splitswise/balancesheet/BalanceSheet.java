package lld.practice.fintech.splitswise.balancesheet;

import java.util.HashMap;
import java.util.Map;

/**
 * Per-user pairwise ledger.
 *
 * <p>For a given owner {@code U}, {@code balances.get(V) = +x} means {@code V owes U x},
 * {@code -x} means {@code U owes V x}. Net-zero entries are pruned to keep
 * the map small.
 *
 * <p>Storing the running balance (rather than re-deriving from the expense
 * list on every read) makes balance lookups O(friends) instead of O(expenses).
 */
public class BalanceSheet {
    private final String ownerUserId;
    private final Map<String, Double> balances = new HashMap<>();

    public BalanceSheet(String ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getOwnerUserId() { return ownerUserId; }
    public Map<String, Double> getBalances() { return new HashMap<>(balances); }

    /**
     * {@code otherUserId} owes the owner {@code delta} more (negative = owner owes them).
     * Net-zero entries are removed.
     */
    public void adjust(String otherUserId, double delta) {
        double next = balances.getOrDefault(otherUserId, 0.0) + delta;
        if (Math.abs(next) < 0.005) {
            balances.remove(otherUserId);
        } else {
            balances.put(otherUserId, next);
        }
    }

    public double balanceWith(String otherUserId) {
        return balances.getOrDefault(otherUserId, 0.0);
    }
}
