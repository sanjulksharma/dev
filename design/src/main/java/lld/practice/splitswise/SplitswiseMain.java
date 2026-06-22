package lld.practice.splitswise;

import lld.practice.splitswise.balancesheet.BalanceSheet;
import lld.practice.splitswise.enums.Currency;
import lld.practice.splitswise.enums.ExpenseType;
import lld.practice.splitswise.enums.StatusType;
import lld.practice.splitswise.expense.Expense;
import lld.practice.splitswise.expense.ResolvedShare;
import lld.practice.splitswise.expense.Share;
import lld.practice.splitswise.group.Group;
import lld.practice.splitswise.services.BalanceService;
import lld.practice.splitswise.services.SplitwiseApp;
import lld.practice.splitswise.settlement.Settlement;
import lld.practice.splitswise.user.User;

import java.util.Arrays;
import java.util.List;

/**
 * End-to-end demo of the Splitwise model.
 *
 * <p>Walks through: creating users + a trip group, adding two expenses
 * (EQUAL and PERCENTAGE splits), running a settlement, and reading back
 * balances + simplified debts.
 */
public class SplitswiseMain {
    public static void main(String[] args) {
        SplitwiseApp app = new SplitwiseApp();

        // 1. Create users.
        User alice = app.users().createUser("alice@x.com", "Alice");
        User bob   = app.users().createUser("bob@x.com",   "Bob");
        User carol = app.users().createUser("carol@x.com", "Carol");

        // 2. Create a trip group.
        Group trip = app.groups().createGroup(
                "Goa Trip", alice, Arrays.asList(alice, bob, carol), StatusType.ACTIVE);

        // 3. Equal-split expense — Alice paid 300 for dinner, split 3 ways.
        Expense dinner = Expense.builder()
                .expenseType(ExpenseType.EQUAL)
                .amount(300.0)
                .currency(Currency.INR)
                .paidBy(alice)
                .createdBy(alice)
                .groupId(trip.getGroupId())
                .description("Dinner")
                .shares(Arrays.asList(
                        new Share(alice.getUserId(), 0),
                        new Share(bob.getUserId(),   0),
                        new Share(carol.getUserId(), 0)))
                .build();
        app.expenses().createExpense(dinner);

        // 4. Percentage-split expense — Bob paid 1000 for the cab. Alice 50%, Bob 25%, Carol 25%.
        Expense cab = Expense.builder()
                .expenseType(ExpenseType.PERCENTAGE)
                .amount(1000.0)
                .currency(Currency.INR)
                .paidBy(bob)
                .createdBy(bob)
                .groupId(trip.getGroupId())
                .description("Cab")
                .shares(Arrays.asList(
                        new Share(alice.getUserId(), 50),
                        new Share(bob.getUserId(),   25),
                        new Share(carol.getUserId(), 25)))
                .build();
        app.expenses().createExpense(cab);

        // 5. Carol pays Alice 100 to settle part of the dinner.
        app.settlements().record(new Settlement(
                trip.getGroupId(), carol, alice, 100.0, Currency.INR, "partial dinner"));

        // 6. Print balances.
        System.out.println("=== Balances ===");
        printSheet(app.balances().getBalanceSheet(alice.getUserId()), "Alice");
        printSheet(app.balances().getBalanceSheet(bob.getUserId()),   "Bob");
        printSheet(app.balances().getBalanceSheet(carol.getUserId()), "Carol");

        // 7. Simplify across the trip.
        System.out.println("\n=== Simplified Debts ===");
        List<BalanceService.SimplifiedDebt> simplified = app.balances()
                .simplifyDebts(Arrays.asList(alice.getUserId(), bob.getUserId(), carol.getUserId()));
        for (BalanceService.SimplifiedDebt d : simplified) {
            System.out.printf("  %s -> %s : %.2f%n", d.fromUserId, d.toUserId, d.amount);
        }

        // 8. Activity feed for Alice.
        System.out.println("\n=== Alice's Activity ===");
        app.activities().getFeedForUser(alice.getUserId())
                .forEach(a -> System.out.printf("  %s %s %.2f %s%n",
                        a.getOperation(), a.getExpenseId(), a.getAmount(), a.getCurrency()));

        // 9. Show resolved shares on the cab expense to confirm splitting worked.
        System.out.println("\n=== Cab resolved shares ===");
        for (ResolvedShare rs : app.expenses().getExpense(cab.getExpenseId()).getResolvedShares()) {
            System.out.printf("  %s owes %.2f%n", rs.getUserId(), rs.getAmount());
        }
    }

    private static void printSheet(BalanceSheet sheet, String label) {
        System.out.println(label + " (" + sheet.getOwnerUserId() + "):");
        sheet.getBalances().forEach((otherId, amount) ->
                System.out.printf("  vs %s : %+.2f%n", otherId, amount));
    }
}
