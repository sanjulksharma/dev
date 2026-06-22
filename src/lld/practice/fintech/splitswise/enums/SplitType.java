package lld.practice.fintech.splitswise.enums;

/**
 * Alias used by the splitting subsystem. Mirrors {@link ExpenseType} so the
 * domain model and the split engine can evolve independently without one
 * dragging unrelated changes into the other (Interface Segregation Principle).
 */
public enum SplitType {
    EQUAL,
    EXACT,
    PERCENTAGE,
    SHARES,
    ADJUSTMENT
}
