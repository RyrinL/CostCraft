package com.costcraft.service.cost;

import com.costcraft.dto.CostLine;
import com.costcraft.entity.BomItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class DirectMaterialsCost implements UnitCostCalculation {

    private final BigDecimal totalDirect;
    private final List<CostLine> breakdown;

    public DirectMaterialsCost(List<BomItem> bomItems) {
        this.breakdown = new ArrayList<>();
        BigDecimal sum = BigDecimal.ZERO;
        for (BomItem bi : bomItems) {
            BigDecimal line = bi.getGramQuantity().multiply(bi.getMaterialUnitCost())
                    .setScale(4, RoundingMode.HALF_UP);
            sum = sum.add(line);
            breakdown.add(new CostLine(bi.getMaterialName(), "STAGE_1", line,
                    bi.getGramQuantity() + " " + bi.getMaterialUnit() + " × " + bi.getMaterialUnitCost()));
        }
        this.totalDirect = sum;
    }

    @Override public BigDecimal getTotalDirect() { return totalDirect; }
    @Override public BigDecimal getTotalOverhead() { return BigDecimal.ZERO; }
    @Override public BigDecimal getUnitCost() { return totalDirect; }
    @Override public List<CostLine> getBreakdown() { return new ArrayList<>(breakdown); }
}
