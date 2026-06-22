# Splitswise

A compact, in-memory Java implementation of a **Splitwise**-style expense splitter — modelled around real-world use (groups, multi-currency, partial settlements, debt simplification) and built around classical design patterns and SOLID principles so it stays easy to extend.

---

## Quick start

```bash
# Compile (Java 8+)
find src -name '*.java' > sources.txt
mkdir -p out && javac -d out @sources.txt

# Run the end-to-end demo
java -cp out Main
```

The demo creates 3 users, a trip group, two expenses (EQUAL + PERCENTAGE splits), a settlement, then prints balances, the simplified-debt graph, and the activity feed.

---

## Package layout

```
src/
├── Main.java                       # end-to-end demo
├── enums/                          # Currency, ExpenseType, SplitType, StatusType
├── user/User.java                  # User entity
├── group/Group.java                # Group entity (enforces membership invariants)
├── expense/
│   ├── Expense.java                # Builder-constructed entity
│   ├── Share.java                  # raw user input
│   ├── ResolvedShare.java          # computed per-user amount
│   ├── ExpenseResponse.java        # DTO
│   └── split/                      # Strategy + Factory for split math
│       ├── SplitStrategy.java
│       ├── EqualSplitStrategy.java
│       ├── ExactSplitStrategy.java
│       ├── PercentageSplitStrategy.java
│       ├── SharesSplitStrategy.java
│       ├── AdjustmentSplitStrategy.java
│       └── SplitStrategyFactory.java
├── balancesheet/BalanceSheet.java  # per-user pairwise ledger
├── settlement/Settlement.java      # immutable payment record
├── activity/Activity.java          # append-only audit row
├── observer/                       # Event bus (Observer pattern)
│   ├── EventBus.java
│   ├── ExpenseEvent.java
│   └── ExpenseObserver.java
├── repository/                     # CRUD abstraction
│   ├── Repository.java
│   └── InMemoryRepository.java
├── exception/                      # typed domain errors
└── services/                       # orchestration layer
    ├── SplitwiseApp.java           # composition root / facade
    ├── UserService.java
    ├── GroupService.java
    ├── ExpenseService.java         # orchestrates split + publish
    ├── BalanceService.java    # ledger + debt simplification
    ├── BalanceObserver.java
    ├── ActivityService.java        # Observer → activity feed
    └── SettlementService.java
```

---

## Design patterns used

| Pattern    | Where                                                                 | Why                                                                                       |
|------------|-----------------------------------------------------------------------|-------------------------------------------------------------------------------------------|
| **Strategy**  | `expense/split/SplitStrategy` + 5 implementations                 | Each split algorithm (Equal, Exact, Percentage, Shares, Adjustment) is interchangeable.   |
| **Factory**   | `SplitStrategyFactory`                                            | Resolves `ExpenseType → SplitStrategy`. Clients depend on the abstraction, not the type.  |
| **Singleton** | `SplitStrategyFactory#getInstance`                                | Strategies are stateless; one shared registry avoids allocations.                         |
| **Builder**   | `Expense.Builder`, `Activity.Builder`                             | Multiple optional fields; constructor would be unreadable.                                |
| **Observer**  | `EventBus`, `ExpenseObserver`, `ActivityService`, `BalanceObserver` | Decouples side-effects (audit, balance, notifications) from the expense lifecycle.      |
| **Repository**| `Repository<K,V>` + `InMemoryRepository`                          | Services depend on storage abstraction; swap in JDBC/JPA later with no service changes.   |
| **Facade**    | `SplitwiseApp`                                                    | Single composition root that callers use instead of wiring 6+ repos and the bus manually. |
| **DTO**       | `ExpenseResponse`                                                 | Decouples wire/UI shape from internal entity.                                              |

---

## SOLID applied

- **S — Single Responsibility**
  - `ExpenseService` only orchestrates. It does not compute splits (`SplitStrategy`), persist balances (`BalanceService`), or log activity (`ActivityService`).
  - `BalanceObserver` and `ActivityService` each handle exactly one side-effect.

- **O — Open/Closed**
  - Adding a new split type: implement `SplitStrategy`, register it in `SplitStrategyFactory`. Existing strategies and `ExpenseService` are untouched.
  - Adding a new side-effect (e.g., a notification service): implement `ExpenseObserver` and subscribe to `EventBus`. No service changes.

- **L — Liskov Substitution**
  - Every `SplitStrategy` honours the same contract (`Σ resolved == total`, validation throws on bad input), so callers can swap them freely.
  - Any `Repository<K,V>` implementation can replace `InMemoryRepository` without breaking services.

- **I — Interface Segregation**
  - `Repository` exposes only CRUD — no leaked query-engine concerns.
  - `ExpenseObserver` exposes one method; observers don't pay the cost of unrelated event types.
  - `SplitType` enum mirrors `ExpenseType` so the split engine can evolve without forcing changes on the domain enum.

- **D — Dependency Inversion**
  - Services depend on `Repository` and `EventBus` interfaces, not concrete classes.
  - `ExpenseService` depends on the abstract `SplitStrategy`; concretes are produced by the factory.

---

## Domain model (concise)

- **User** — identified by `userId`; equality based on id only.
- **Group** — owns its member list; `addUser`/`removeUser` enforce invariants. `isMember()` is the authorization primitive.
- **Expense** — immutable except for `resolvedShares`, which the service sets exactly once after the strategy runs. Built via `Expense.builder()`.
- **Share** — raw user input (% / shares / exact / adjustment).
- **ResolvedShare** — concrete amount each participant owes; produced by `SplitStrategy.resolve(...)`.
- **BalanceSheet** — per-user `Map<otherUserId, signedAmount>`; `+x` means *other owes me x*.
- **Settlement** — immutable cash-payment record; reduces balances on both sides in one call.
- **Activity** — append-only audit row written by the activity observer.

### Money & rounding

Amounts are stored as `double` but all split math is done in **integer cents** to avoid floating drift. Rounding remainder is deterministically assigned (to the first/last participant depending on strategy), and `ExpenseService` re-asserts `Σ shares == total` after the strategy returns.

---

## How the write path flows

```
client → ExpenseService.createExpense(expense)
            │
            ├─ ensureMembership(...)
            ├─ SplitStrategyFactory.getStrategy(type).validate(...)
            ├─ strategy.resolve(...)                  ─►  List<ResolvedShare>
            ├─ verifySumEqualsTotal(...)
            ├─ expenseRepository.save(...)
            └─ eventBus.publish(CREATED, expense)
                        │
                        ├─ BalanceObserver       → balanceSheet.adjust(...)
                        └─ ActivityService       → activity row written
```

Updates are modeled as `DELETED` (revert) + `CREATED` (apply) on the bus, so the ledger and audit never diverge.

---

## Debt simplification

`BalanceService.simplifyDebts(userIds)` returns the minimum cash-flow set of edges that settles every debt:

1. Compute each user's **net** position (sum across all friends).
2. Push creditors and debtors into two max-heaps by magnitude.
3. Repeatedly take the largest creditor + largest debtor, record `debtor → creditor : min(amounts)`, reinsert the remainder.

Runs in `O(N log N)` on the participant set.

---

## Extending the system

- **New split rule** — implement `SplitStrategy`, register in `SplitStrategyFactory`, add the enum value. Done.
- **Notifications** — implement `ExpenseObserver`, subscribe to `EventBus`. No service changes.
- **Real storage** — implement `Repository<K,V>` against your DB, plug into `SplitwiseApp` instead of `InMemoryRepository`.
- **Async events** — replace synchronous `EventBus` with a Kafka-backed implementation behind the same interface.

---

## Demo output (sample)

```
=== Balances ===
Alice: vs Bob -400.00
Bob:   vs Alice +400.00, vs Carol +250.00
Carol: vs Bob -250.00

=== Simplified Debts ===
  Alice -> Bob : 400.00
  Carol -> Bob : 250.00
```
