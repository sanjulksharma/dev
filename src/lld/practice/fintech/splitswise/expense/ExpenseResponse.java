package lld.practice.fintech.splitswise.expense;

import lld.practice.fintech.splitswise.enums.Currency;
import lld.practice.fintech.splitswise.enums.ExpenseType;
import lld.practice.fintech.splitswise.user.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO returned to callers — decouples the wire/UI representation from the
 * internal {@link Expense} entity (Single Responsibility: model holds state,
 * response handles presentation).
 */
public class ExpenseResponse {
    private String expenseId;
    private ExpenseType expenseType;
    private double amount;
    private User paidBy;
    private User createdBy;
    private String groupId;
    private Currency currency;
    private String description;
    private List<ResolvedShare> shareResponse;

    public String getExpenseId() { return expenseId; }
    public ExpenseType getExpenseType() { return expenseType; }
    public double getAmount() { return amount; }
    public User getPaidBy() { return paidBy; }
    public User getCreatedBy() { return createdBy; }
    public String getGroupId() { return groupId; }
    public Currency getCurrency() { return currency; }
    public String getDescription() { return description; }
    public List<ResolvedShare> getShareResponse() { return shareResponse; }

    /**
     * Adapter from internal entity to wire response. Centralizing the mapping
     * here ensures every caller sees a consistent projection.
     */
    public static ExpenseResponse fromExpense(Expense expense) {
        ExpenseResponse r = new ExpenseResponse();
        r.expenseId = expense.getExpenseId();
        r.expenseType = expense.getExpenseType();
        r.amount = expense.getAmount();
        r.paidBy = expense.getPaidBy();
        r.createdBy = expense.getCreatedBy();
        r.groupId = expense.getGroupId();
        r.currency = expense.getCurrency();
        r.description = expense.getDescription();
        r.shareResponse = expense.getResolvedShares().stream()
                .map(rs -> new ResolvedShare(rs.getUserId(), rs.getAmount()))
                .collect(Collectors.toList());
        return r;
    }
}
