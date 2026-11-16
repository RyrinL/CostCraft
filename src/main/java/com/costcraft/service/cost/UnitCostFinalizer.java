package com.costcraft.service.cost;

import com.costcraft.dto.CostLine;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class UnitCostFinalizer extends AbstractCostDecorator {

    private final BigDecimal unitCost;

    public UnitCostFinalizer(UnitCostCalculation wrapped, BigDecimal yieldPerBatch) {
        super(wrapped);
        if (yieldPerBatch != null && yieldPerBatch.compareTo(BigDecimal.ZERO) > 0) {
            this.unitCost = wrapped.getUnitCost()
                    .divide(yieldPerBatch, 4, RoundingMode.HALF_UP);
        } else {
            this.unitCost = wrapped.getUnitCost();
        }
    }

    @Override
    public BigDecimal getUnitCost() { return unitCost; }

    @Override
    public List<CostLine> getBreakdown() { return wrapped.getBreakdown(); }
}
