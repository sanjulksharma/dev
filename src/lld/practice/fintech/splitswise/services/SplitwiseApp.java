package lld.practice.fintech.splitswise.services;

import lld.practice.fintech.splitswise.activity.Activity;
import lld.practice.fintech.splitswise.balancesheet.BalanceSheet;
import lld.practice.fintech.splitswise.expense.Expense;
import lld.practice.fintech.splitswise.group.Group;
import lld.practice.fintech.splitswise.observer.EventBus;
import lld.practice.fintech.splitswise.repository.InMemoryRepository;
import lld.practice.fintech.splitswise.repository.Repository;
import lld.practice.fintech.splitswise.settlement.Settlement;
import lld.practice.fintech.splitswise.user.User;

/**
 * Composition root — builds all collaborators and wires the observer chain.
 *
 * <p>Acts as a Facade: callers (e.g., a REST controller or {@code Main})
 * interact with a small set of public services instead of constructing every
 * repository, event bus, and observer themselves.
 */
public class SplitwiseApp {

    private final UserService userService;
    private final GroupService groupService;
    private final ExpenseService expenseService;
    private final BalanceService balanceService;
    private final SettlementService settlementService;
    private final ActivityService activityService;
    private final EventBus eventBus;

    public SplitwiseApp() {
        Repository<String, User> userRepo = new InMemoryRepository<>();
        Repository<String, Group> groupRepo = new InMemoryRepository<>();
        Repository<String, Expense> expenseRepo = new InMemoryRepository<>();
        Repository<String, BalanceSheet> balanceRepo = new InMemoryRepository<>();
        Repository<String, Settlement> settlementRepo = new InMemoryRepository<>();
        Repository<String, Activity> activityRepo = new InMemoryRepository<>();

        this.eventBus = new EventBus();
        this.userService = new UserService(userRepo);
        this.groupService = new GroupService(groupRepo);
        this.balanceService = new BalanceService(balanceRepo);
        this.activityService = new ActivityService(activityRepo);
        this.settlementService = new SettlementService(settlementRepo, balanceService);
        this.expenseService = new ExpenseService(expenseRepo, groupService, eventBus);

        // Subscribe observers — order doesn't matter functionally, but balance-first
        // makes for easier debugging since the ledger is settled before the audit row writes.
        eventBus.subscribe(new BalanceObserver(balanceService));
        eventBus.subscribe(activityService);
    }

    public UserService users() { return userService; }
    public GroupService groups() { return groupService; }
    public ExpenseService expenses() { return expenseService; }
    public BalanceService balances() { return balanceService; }
    public SettlementService settlements() { return settlementService; }
    public ActivityService activities() { return activityService; }
    public EventBus eventBus() { return eventBus; }
}
