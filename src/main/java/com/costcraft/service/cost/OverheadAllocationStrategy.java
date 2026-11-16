package com.costcraft.service.cost;

import com.costcraft.dto.BatchContext;

import java.math.BigDecimal;

public interface OverheadAllocationStrategy {
    BigDecimal allocate(BatchContext ctx);
    String describe();
}
