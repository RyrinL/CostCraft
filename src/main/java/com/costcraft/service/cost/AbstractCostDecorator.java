package com.costcraft.service.cost;

import com.costcraft.dto.CostLine;

import java.math.BigDecimal;
import java.util.List;

public abstract class AbstractCostDecorator implements UnitCostCalculation {

    protected final UnitCostCalculation wrapped;

    protected AbstractCostDecorator(UnitCostCalculation wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public BigDecimal getTotalDirect() { return wrapped.getTotalDirect(); }

    @Override
    public BigDecimal getTotalOverhead() { return wrapped.getTotalOverhead(); }

    @Override
    public BigDecimal getUnitCost() { return wrapped.getUnitCost(); }

    @Override
    public List<CostLine> getBreakdown() { return wrapped.getBreakdown(); }
}
