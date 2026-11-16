package com.costcraft.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class UnitCostBreakdown {
    public Long productId;
    public String productName;
    public BigDecimal yieldPerBatch;

    public BigDecimal stage1Materials = BigDecimal.ZERO;
    public BigDecimal stage2OtherDirect = BigDecimal.ZERO;
    public BigDecimal totalDirect = BigDecimal.ZERO;
    public BigDecimal stage3Overhead = BigDecimal.ZERO;
    public BigDecimal totalCost = BigDecimal.ZERO;
    public BigDecimal unitCost = BigDecimal.ZERO;
    public BigDecimal priceLower = BigDecimal.ZERO;
    public BigDecimal priceUpper = BigDecimal.ZERO;

    public List<CostLine> breakdown = new ArrayList<>();

    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public BigDecimal getYieldPerBatch() { return yieldPerBatch; }
    public BigDecimal getStage1Materials() { return stage1Materials; }
    public BigDecimal getStage2OtherDirect() { return stage2OtherDirect; }
    public BigDecimal getTotalDirect() { return totalDirect; }
    public BigDecimal getStage3Overhead() { return stage3Overhead; }
    public BigDecimal getTotalCost() { return totalCost; }
    public BigDecimal getUnitCost() { return unitCost; }
    public BigDecimal getPriceLower() { return priceLower; }
    public BigDecimal getPriceUpper() { return priceUpper; }
    public List<CostLine> getBreakdown() { return breakdown; }
}
