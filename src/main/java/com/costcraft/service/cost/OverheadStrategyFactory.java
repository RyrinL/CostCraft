package com.costcraft.service.cost;

import com.costcraft.entity.OverheadConfig;

public class OverheadStrategyFactory {

    private OverheadStrategyFactory() {}

    public static OverheadAllocationStrategy forConfig(OverheadConfig cfg) {
        return switch (cfg.getAllocationRule()) {
            case "PER_BATCH_FIXED"       -> new PerBatchFixedStrategy(cfg.getFixedAmount(), cfg.getName());
            case "PER_UNIT_FIXED"        -> new PerUnitFixedStrategy(cfg.getFixedAmount(), cfg.getName());
            case "DAILY_ALLOCATED"       -> new DailyAllocatedStrategy(cfg.getDailyAmount(),
                                                cfg.getDaysPerMonth(), cfg.getName());
            case "PERCENTAGE_OF_DIRECT"  -> new PercentageOfDirectStrategy(cfg.getPercentage(), cfg.getName());
            default -> throw new IllegalArgumentException("Unknown allocation rule: " + cfg.getAllocationRule());
        };
    }
}
