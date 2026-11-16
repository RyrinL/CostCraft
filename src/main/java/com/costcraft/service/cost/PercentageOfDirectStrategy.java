package com.costcraft.service.cost;

import com.costcraft.dto.BatchContext;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PercentageOfDirectStrategy implements OverheadAllocationStrategy {

    private final BigDecimal percentage;
    private final String name;

    public PercentageOfDirectStrategy(BigDecimal percentage, String name) {
        this.percentage = percentage;
        this.name = name;
    }

    @Override
    public BigDecimal allocate(BatchContext ctx) {
        if (percentage == null || ctx.getTotalDirectCost() == null) return BigDecimal.ZERO;
        return percentage.multiply(ctx.getTotalDirectCost()).setScale(4, RoundingMode.HALF_UP);
    }

    @Override
    public String describe() {
        BigDecimal pct = percentage != null ? percentage.multiply(BigDecimal.valueOf(100)) : BigDecimal.ZERO;
        return name + ": " + pct.stripTrailingZeros().toPlainString() + "% of total direct";
    }
}
