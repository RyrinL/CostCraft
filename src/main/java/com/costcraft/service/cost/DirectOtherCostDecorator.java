package com.costcraft.service.cost;

import com.costcraft.dto.BatchContext;
import com.costcraft.dto.CostLine;
import com.costcraft.entity.OverheadConfig;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class DirectOtherCostDecorator extends AbstractCostDecorator {

    private final OverheadConfig config;
    private final BigDecimal addedCost;

    public DirectOtherCostDecorator(UnitCostCalculation wrapped, OverheadConfig config, BatchContext ctx) {
        super(wrapped);
        this.config = config;
        OverheadAllocationStrategy strategy = OverheadStrategyFactory.forConfig(config);
        this.addedCost = strategy.allocate(ctx).setScale(4, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getTotalDirect() {
        return wrapped.getTotalDirect().add(addedCost);
    }

    @Override
    public BigDecimal getUnitCost() {
        return wrapped.getUnitCost().add(addedCost);
    }

    @Override
    public List<CostLine> getBreakdown() {
        List<CostLine> lines = new ArrayList<>(wrapped.getBreakdown());
        lines.add(new CostLine(config.getName(), "STAGE_2", addedCost, config.getAllocationRule()));
        return lines;
    }
}
