package lld.practice.fintech.splitswise.observer;

import lld.practice.fintech.splitswise.expense.Expense;

/**
 * Value object passed to {@link ExpenseObserver}s. Carries the expense plus
 * the operation that happened (CREATED/UPDATED/DELETED/SETTLED).
 */
public final class ExpenseEvent {
    public enum Type { CREATED, UPDATED, DELETED, SETTLED }

    private final Type type;
    private final Expense expense;
    private final String actorUserId;

    public ExpenseEvent(Type type, Expense expense, String actorUserId) {
        this.type = type;
        this.expense = expense;
        this.actorUserId = actorUserId;
    }

    public Type getType() { return type; }
    public Expense getExpense() { return expense; }
    public String getActorUserId() { return actorUserId; }
}
