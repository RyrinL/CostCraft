package com.costcraft.service.cost;

import com.costcraft.dto.BatchContext;

import java.math.BigDecimal;

public class PerBatchFixedStrategy implements OverheadAllocationStrategy {

    private final BigDecimal fixedAmount;
    private final String name;

    public PerBatchFixedStrategy(BigDecimal fixedAmount, String name) {
        this.fixedAmount = fixedAmount;
        this.name = name;
    }

    @Override
    public BigDecimal allocate(BatchContext ctx) {
        return fixedAmount != null ? fixedAmount : BigDecimal.ZERO;
    }

    @Override
    public String describe() { return name + ": fixed " + fixedAmount + " per batch"; }
}
