package lld.practice.fintech.splitswise.services;

import lld.practice.fintech.splitswise.activity.Activity;
import lld.practice.fintech.splitswise.observer.ExpenseEvent;
import lld.practice.fintech.splitswise.observer.ExpenseObserver;
import lld.practice.fintech.splitswise.repository.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Observer implementation — converts expense events into immutable {@link Activity}
 * records and persists them. The {@link services.ExpenseService} doesn't know we exist;
 * it only publishes events to the bus (Open/Closed: new consumers added freely).
 */
public class ActivityService implements ExpenseObserver {

    private final Repository<String, Activity> activityRepository;

    public ActivityService(Repository<String, Activity> activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public void onEvent(ExpenseEvent event) {
        Activity activity = Activity.builder()
                .userId(event.getActorUserId())
                .expenseId(event.getExpense().getExpenseId())
                .amount(event.getExpense().getAmount())
                .currency(event.getExpense().getCurrency())
                .operation(event.getType().name())
                .build();
        activityRepository.save(activity.getActivityId(), activity);
    }

    public Collection<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    /** Per-user feed sorted newest first. */
    public List<Activity> getFeedForUser(String userId) {
        List<Activity> out = new ArrayList<>();
        for (Activity a : activityRepository.findAll()) {
            if (userId.equals(a.getUserId())) out.add(a);
        }
        out.sort(Comparator.comparingLong(Activity::getTimestampMillis).reversed());
        return out;
    }
}
