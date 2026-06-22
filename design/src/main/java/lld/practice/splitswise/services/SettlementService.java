package lld.practice.splitswise.services;

import lld.practice.splitswise.repository.Repository;
import lld.practice.splitswise.settlement.Settlement;

import java.util.Collection;

/**
 * Records cash settlements. Updates balances synchronously through
 * {@link BalanceService}.
 *
 * <p>Settlements bypass the {@link observer.EventBus} for now — they don't run
 * through split strategies, and observers that care about settlements can be
 * added in a follow-up by introducing a SettlementEvent.
 */
public class SettlementService {

    private final Repository<String, Settlement> settlementRepository;
    private final BalanceService balanceService;

    public SettlementService(
            Repository<String, Settlement> settlementRepository,
            BalanceService balanceService) {
        this.settlementRepository = settlementRepository;
        this.balanceService = balanceService;
    }

    public Settlement record(Settlement settlement) {
        settlementRepository.save(settlement.getSettlementId(), settlement);
        balanceService.applySettlement(settlement);
        return settlement;
    }

    public Collection<Settlement> getAll() {
        return settlementRepository.findAll();
    }
}
