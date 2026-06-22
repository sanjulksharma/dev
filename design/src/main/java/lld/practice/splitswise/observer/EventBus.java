package lld.practice.splitswise.observer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Simple synchronous event bus (Subject side of Observer pattern).
 *
 * <p>Production wiring would swap this for Kafka — but the
 * service code wouldn't change because it depends on this interface, not the
 * transport (Dependency Inversion Principle).
 */
public class EventBus {

    private final List<ExpenseObserver> observers = new CopyOnWriteArrayList<>();

    public void subscribe(ExpenseObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(ExpenseObserver observer) {
        observers.remove(observer);
    }

    public void publish(ExpenseEvent event) {
        // CopyOnWriteArrayList is safe to iterate while observers register/unregister.
        for (ExpenseObserver o : observers) {
            o.onEvent(event);
        }
    }
}
