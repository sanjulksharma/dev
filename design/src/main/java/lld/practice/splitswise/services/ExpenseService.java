package lld.practice.splitswise.services;

import lld.practice.splitswise.exception.EntityNotFoundException;
import lld.practice.splitswise.exception.InvalidSplitException;
import lld.practice.splitswise.expense.Expense;
import lld.practice.splitswise.expense.ResolvedShare;
import lld.practice.splitswise.expense.Share;
import lld.practice.splitswise.expense.split.SplitStrategy;
import lld.practice.splitswise.expense.split.SplitStrategyFactory;
import lld.practice.splitswise.group.Group;
import lld.practice.splitswise.observer.EventBus;
import lld.practice.splitswise.observer.ExpenseEvent;
import lld.practice.splitswise.repository.Repository;
import lld.practice.splitswise.user.User;

import java.util.List;

/**
 * Orchestrates expense create/read/update/delete.
 *
 * <p>Composition of patterns:
 * <ul>
 *   <li><b>Strategy + Factory</b> — split math is delegated to a {@link SplitStrategy}
 *       resolved through {@link SplitStrategyFactory}. Adding a new split type
 *       requires no changes here.</li>
 *   <li><b>Observer</b> — side effects (audit log, balance update, notifications)
 *       are decoupled via {@link EventBus}.</li>
 *   <li><b>Builder</b> — expenses are constructed via {@link Expense.Builder}.</li>
 * </ul>
 *
 * <p>SOLID:
 * <ul>
 *   <li><b>SRP</b> — this class only orchestrates; it doesn't compute splits or log activity.</li>
 *   <li><b>DIP</b> — depends on {@link Repository} and {@link EventBus} abstractions.</li>
 *   <li><b>OCP</b> — new split strategies and new observers plug in without modifying this class.</li>
 * </ul>
 */
public class ExpenseService {

    private final Repository<String, Expense> expenseRepository;
    private final GroupService groupService;
    private final EventBus eventBus;
    private final SplitStrategyFactory splitFactory;

    public ExpenseService(
            Repository<String, Expense> expenseRepository,
            GroupService groupService,
            EventBus eventBus) {
        this.expenseRepository = expenseRepository;
        this.groupService = groupService;
        this.eventBus = eventBus;
        this.splitFactory = SplitStrategyFactory.getInstance();
    }

    /**
     * Validates the request, runs the appropriate split strategy, persists the
     * expense, and publishes a CREATED event.
     */
    public Expense createExpense(Expense expense) {
        ensureMembership(expense);

        SplitStrategy strategy = splitFactory.getStrategy(expense.getExpenseType());
        strategy.validate(expense.getAmount(), expense.getShares());
        List<ResolvedShare> resolved = strategy.resolve(expense.getAmount(), expense.getShares());
        verifySumEqualsTotal(resolved, expense.getAmount());

        expense.setResolvedShares(resolved);
        expenseRepository.save(expense.getExpenseId(), expense);

        eventBus.publish(new ExpenseEvent(
                ExpenseEvent.Type.CREATED, expense, expense.getCreatedBy().getUserId()));
        return expense;
    }

    public Expense getExpense(String expenseId) {
        return expenseRepository.findById(expenseId)
                .orElseThrow(() -> new EntityNotFoundException("Expense", expenseId));
    }

    /**
     * Update is modeled as revert + apply so balance math stays consistent.
     * We publish DELETED then CREATED so the observer chain sees the same
     * before/after view as if it were a fresh expense.
     */
    public Expense updateExpense(Expense updated) {
        Expense existing = getExpense(updated.getExpenseId());
        // Revert the old expense's effects first.
        eventBus.publish(new ExpenseEvent(
                ExpenseEvent.Type.DELETED, existing, updated.getCreatedBy().getUserId()));

        // Now apply the new version.
        return createExpense(updated);
    }

    public void deleteExpense(String expenseId, User actor) {
        Expense expense = getExpense(expenseId);
        expenseRepository.deleteById(expenseId);
        eventBus.publish(new ExpenseEvent(
                ExpenseEvent.Type.DELETED, expense, actor.getUserId()));
    }

    private void ensureMembership(Expense expense) {
        if (expense.getGroupId() == null) {
            return; // 1:1 expenses don't need a group.
        }
        Group group = groupService.getGroup(expense.getGroupId());
        if (!group.isMember(expense.getPaidBy())) {
            throw new InvalidSplitException("Payer is not a member of the group");
        }
        for (Share share : expense.getShares()) {
            if (!group.isMember(share.getUserId())) {
                throw new InvalidSplitException(
                        "Share user " + share.getUserId() + " is not a group member");
            }
        }
    }

    private void verifySumEqualsTotal(List<ResolvedShare> shares, double total) {
        double sum = shares.stream().mapToDouble(ResolvedShare::getAmount).sum();
        if (Math.abs(sum - total) > 0.01) {
            throw new InvalidSplitException(
                    "Split strategy produced sum " + sum + " != total " + total);
        }
    }
}
