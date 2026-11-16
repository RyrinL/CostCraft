package com.costcraft.service.cost;

import com.costcraft.dto.BatchContext;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PerUnitFixedStrategy implements OverheadAllocationStrategy {

    private final BigDecimal fixedAmount;
    private final String name;

    public PerUnitFixedStrategy(BigDecimal fixedAmount, String name) {
        this.fixedAmount = fixedAmount;
        this.name = name;
    }

    @Override
    public BigDecimal allocate(BatchContext ctx) {
        if (fixedAmount == null || ctx.getYieldPerBatch() == null) return BigDecimal.ZERO;
        return fixedAmount.multiply(ctx.getYieldPerBatch()).setScale(4, RoundingMode.HALF_UP);
    }

    @Override
    public String describe() { return name + ": " + fixedAmount + " per unit × yield"; }
}
