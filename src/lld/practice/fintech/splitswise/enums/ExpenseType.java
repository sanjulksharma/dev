package lld.practice.fintech.splitswise.enums;

/**
 * Split strategy that the user picked when creating the expense.
 *
 * <p>Each value maps 1:1 to a {@code SplitStrategy} implementation; see
 * {@code SplitStrategyFactory}. Adding a new split type requires only:
 * adding an enum value, a new strategy class, and a registration in the factory.
 * (Open/Closed Principle — existing strategies are untouched.)
 */
public enum ExpenseType {
    EQUAL,
    EXACT,
    PERCENTAGE,
    SHARES,
    ADJUSTMENT
}
