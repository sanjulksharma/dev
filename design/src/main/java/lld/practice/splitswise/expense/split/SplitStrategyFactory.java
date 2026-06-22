package lld.practice.splitswise.expense.split;

import lld.practice.splitswise.enums.ExpenseType;
import lld.practice.splitswise.exception.SplitwiseException;

import java.util.EnumMap;
import java.util.Map;

/**
 * Factory pattern — resolves an {@link ExpenseType} to its strategy.
 *
 * <p>Callers depend on the abstraction ({@code SplitStrategy}) rather than
 * concrete classes (Dependency Inversion Principle). Adding a new split type
 * is local: register one new entry here.
 *
 * <p>Implemented as a Singleton with an internal registry — the strategies are
 * stateless, so a single shared instance is safe and saves allocations.
 */
public final class SplitStrategyFactory {

    private static final SplitStrategyFactory INSTANCE = new SplitStrategyFactory();
    private final Map<ExpenseType, SplitStrategy> registry = new EnumMap<>(ExpenseType.class);

    private SplitStrategyFactory() {
        registry.put(ExpenseType.EQUAL, new EqualSplitStrategy());
        registry.put(ExpenseType.EXACT, new ExactSplitStrategy());
        registry.put(ExpenseType.PERCENTAGE, new PercentageSplitStrategy());
        registry.put(ExpenseType.SHARES, new SharesSplitStrategy());
        registry.put(ExpenseType.ADJUSTMENT, new AdjustmentSplitStrategy());
    }

    public static SplitStrategyFactory getInstance() { return INSTANCE; }

    public SplitStrategy getStrategy(ExpenseType type) {
        SplitStrategy s = registry.get(type);
        if (s == null) {
            throw new SplitwiseException("No SplitStrategy registered for " + type);
        }
        return s;
    }
}
