package lld.practice.fintech.splitswise.services;

import lld.practice.fintech.splitswise.balancesheet.BalanceSheet;
import lld.practice.fintech.splitswise.expense.Expense;
import lld.practice.fintech.splitswise.expense.ResolvedShare;
import lld.practice.fintech.splitswise.repository.Repository;
import lld.practice.fintech.splitswise.settlement.Settlement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Maintains per-user pairwise balances and serves the read path
 * (per-friend totals, per-group totals, debt simplification).
 *
 * <p>Writes are driven by {@link #applyExpense(Expense)} and
 * {@link #applySettlement(Settlement)} — both called from inside the
 * transaction in the corresponding service, so the ledger never drifts from
 * the source of truth.
 *
 * <p>Single Responsibility: this class only owns balance state. Activity logging,
 * notifications, etc. live in other observers.
 */
public class BalanceService {

    private final Repository<String, BalanceSheet> sheetRepository;

    public BalanceService(Repository<String, BalanceSheet> sheetRepository) {
        this.sheetRepository = sheetRepository;
    }

    /**
     * Apply an expense to the ledger: each participant owes the payer their
     * resolved share (minus their own if they're the payer).
     */
    public void applyExpense(Expense expense) {
        String payerId = expense.getPaidBy().getUserId();
        for (ResolvedShare rs : expense.getResolvedShares()) {
            if (rs.getUserId().equals(payerId)) continue;
            // payer -> +amount (payer is owed); participant -> -amount (participant owes)
            getOrCreate(payerId).adjust(rs.getUserId(), rs.getAmount());
            getOrCreate(rs.getUserId()).adjust(payerId, -rs.getAmount());
        }
    }

    /** Reverse the effect of an expense (used on update/delete). */
    public void revertExpense(Expense expense) {
        String payerId = expense.getPaidBy().getUserId();
        for (ResolvedShare rs : expense.getResolvedShares()) {
            if (rs.getUserId().equals(payerId)) continue;
            getOrCreate(payerId).adjust(rs.getUserId(), -rs.getAmount());
            getOrCreate(rs.getUserId()).adjust(payerId, rs.getAmount());
        }
    }

    /** Apply a settlement: payer pays payee, reducing payer's debt to payee. */
    public void applySettlement(Settlement s) {
        String payerId = s.getPayer().getUserId();
        String payeeId = s.getPayee().getUserId();
        // payer owed payee, so payer's balance with payee goes up by amount
        getOrCreate(payerId).adjust(payeeId, s.getAmount());
        getOrCreate(payeeId).adjust(payerId, -s.getAmount());
    }

    public BalanceSheet getBalanceSheet(String userId) {
        return getOrCreate(userId);
    }

    /**
     * Minimum cash-flow debt simplification across the given users.
     *
     * <p>Computes each user's net (sum of balances) and greedily settles the
     * largest creditor with the largest debtor until everyone nets zero.
     * Runs in O(N log N) on the participant set. Returns the list of
     * "from -> to : amount" edges that, if paid, settle every debt.
     */
    public List<SimplifiedDebt> simplifyDebts(List<String> userIds) {
        Map<String, Double> net = new HashMap<>();
        for (String uid : userIds) {
            BalanceSheet sheet = getOrCreate(uid);
            double total = sheet.getBalances().values().stream().mapToDouble(Double::doubleValue).sum();
            net.put(uid, total);
        }

        // Max-heaps by absolute amount: one for creditors, one for debtors.
        PriorityQueue<UserAmount> creditors = new PriorityQueue<>((a, b) -> Double.compare(b.amount, a.amount));
        PriorityQueue<UserAmount> debtors = new PriorityQueue<>((a, b) -> Double.compare(b.amount, a.amount));
        for (Map.Entry<String, Double> e : net.entrySet()) {
            if (e.getValue() > 0.005) creditors.add(new UserAmount(e.getKey(), e.getValue()));
            else if (e.getValue() < -0.005) debtors.add(new UserAmount(e.getKey(), -e.getValue()));
        }

        List<SimplifiedDebt> result = new ArrayList<>();
        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            UserAmount c = creditors.poll();
            UserAmount d = debtors.poll();
            double settled = Math.min(c.amount, d.amount);
            result.add(new SimplifiedDebt(d.userId, c.userId, settled));
            if (c.amount - settled > 0.005) creditors.add(new UserAmount(c.userId, c.amount - settled));
            if (d.amount - settled > 0.005) debtors.add(new UserAmount(d.userId, d.amount - settled));
        }
        return result;
    }

    private BalanceSheet getOrCreate(String userId) {
        return sheetRepository.findById(userId).orElseGet(() -> {
            BalanceSheet sheet = new BalanceSheet(userId);
            return sheetRepository.save(userId, sheet);
        });
    }

    private static final class UserAmount {
        final String userId;
        final double amount;
        UserAmount(String userId, double amount) {
            this.userId = userId;
            this.amount = amount;
        }
    }

    /** Result row from {@link #simplifyDebts}. */
    public static final class SimplifiedDebt {
        public final String fromUserId;
        public final String toUserId;
        public final double amount;

        public SimplifiedDebt(String fromUserId, String toUserId, double amount) {
            this.fromUserId = fromUserId;
            this.toUserId = toUserId;
            this.amount = amount;
        }
    }
}
