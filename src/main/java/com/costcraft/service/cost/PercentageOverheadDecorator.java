package com.costcraft.service.cost;

import com.costcraft.dto.BatchContext;
import com.costcraft.dto.CostLine;
import com.costcraft.entity.OverheadConfig;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class PercentageOverheadDecorator extends AbstractCostDecorator {

    private final OverheadConfig config;
    private final BigDecimal addedCost;

    public PercentageOverheadDecorator(UnitCostCalculation wrapped, OverheadConfig config, BatchContext ctx) {
        super(wrapped);
        this.config = config;
        // Strategy uses totalDirect from the wrapped (after Stage 2)
        BatchContext ctxWithDirect = new BatchContext(
                wrapped.getTotalDirect(),
                ctx.getYieldPerBatch(),
                ctx.getDaysPerBatch(),
                ctx.getMultiplier()
        );
        OverheadAllocationStrategy strategy = OverheadStrategyFactory.forConfig(config);
        this.addedCost = strategy.allocate(ctxWithDirect).setScale(4, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getTotalOverhead() {
        return wrapped.getTotalOverhead().add(addedCost);
    }

    @Override
    public BigDecimal getUnitCost() {
        return wrapped.getUnitCost().add(addedCost);
    }

    @Override
    public List<CostLine> getBreakdown() {
        List<CostLine> lines = new ArrayList<>(wrapped.getBreakdown());
        BigDecimal pct = config.getPercentage() != null
                ? config.getPercentage().multiply(BigDecimal.valueOf(100)) : BigDecimal.ZERO;
        lines.add(new CostLine(config.getName(), "STAGE_3", addedCost,
                pct.stripTrailingZeros().toPlainString() + "% of total direct"));
        return lines;
    }
}
