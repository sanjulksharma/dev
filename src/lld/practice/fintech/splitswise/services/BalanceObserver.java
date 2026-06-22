package lld.practice.fintech.splitswise.services;

import lld.practice.fintech.splitswise.observer.ExpenseEvent;
import lld.practice.fintech.splitswise.observer.ExpenseObserver;

/**
 * Observer that keeps {@link BalanceService} in sync as expenses move through
 * their lifecycle. Lives in the service package because it works hand-in-hand
 * with the balance service.
 *
 * <p>Splitting "log activity" and "update balances" into separate observers
 * keeps each class single-purpose (Single Responsibility Principle).
 */
public class BalanceObserver implements ExpenseObserver {

    private final BalanceService balanceService;

    public BalanceObserver(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @Override
    public void onEvent(ExpenseEvent event) {
        switch (event.getType()) {
            case CREATED:
                balanceService.applyExpense(event.getExpense());
                break;
            case DELETED:
                balanceService.revertExpense(event.getExpense());
                break;
            case UPDATED:
            case SETTLED:
                // ExpenseService handles UPDATED by emitting DELETED+CREATED.
                break;
        }
    }
}
