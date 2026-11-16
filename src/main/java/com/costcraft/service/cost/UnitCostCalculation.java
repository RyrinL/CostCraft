package com.costcraft.service.cost;

import com.costcraft.dto.CostLine;

import java.math.BigDecimal;
import java.util.List;

public interface UnitCostCalculation {
    BigDecimal getTotalDirect();
    BigDecimal getTotalOverhead();
    BigDecimal getUnitCost();
    List<CostLine> getBreakdown();
}
