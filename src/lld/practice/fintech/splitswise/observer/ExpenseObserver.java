package lld.practice.fintech.splitswise.observer;

/**
 * Observer pattern — subscribers receive expense lifecycle events.
 *
 * <p>Used to decouple side effects (audit logging, notifications, balance
 * recomputation) from the {@code ExpenseManager}. The service knows nothing
 * about its consumers — it just publishes events.
 */
public interface ExpenseObserver {
    void onEvent(ExpenseEvent event);
}
