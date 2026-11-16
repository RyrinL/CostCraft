package com.costcraft.dto;

import java.math.BigDecimal;

public class BatchContext {
    private BigDecimal totalDirectCost;
    private BigDecimal yieldPerBatch;
    private BigDecimal daysPerBatch;
    private BigDecimal multiplier;

    public BatchContext(BigDecimal totalDirectCost, BigDecimal yieldPerBatch,
                        BigDecimal daysPerBatch, BigDecimal multiplier) {
        this.totalDirectCost = totalDirectCost;
        this.yieldPerBatch = yieldPerBatch;
        this.daysPerBatch = daysPerBatch;
        this.multiplier = multiplier;
    }

    public BigDecimal getTotalDirectCost() { return totalDirectCost; }
    public BigDecimal getYieldPerBatch() { return yieldPerBatch; }
    public BigDecimal getDaysPerBatch() { return daysPerBatch; }
    public BigDecimal getMultiplier() { return multiplier; }
}
