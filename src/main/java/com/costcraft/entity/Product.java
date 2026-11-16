package com.costcraft.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Product {
    private Long id;
    private String name;
    private BigDecimal yieldPerBatch;
    private String yieldUnit;
    private BigDecimal daysPerBatch;
    private BigDecimal marginLowerPct;
    private BigDecimal marginUpperPct;
    private BigDecimal cachedTotalDirect;
    private BigDecimal cachedUnitCost;
    private BigDecimal cachedPriceLower;
    private BigDecimal cachedPriceUpper;
    private boolean active;
    private LocalDateTime createdAt;

    public Product() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getYieldPerBatch() { return yieldPerBatch; }
    public void setYieldPerBatch(BigDecimal yieldPerBatch) { this.yieldPerBatch = yieldPerBatch; }

    public String getYieldUnit() { return yieldUnit; }
    public void setYieldUnit(String yieldUnit) { this.yieldUnit = yieldUnit; }

    public BigDecimal getDaysPerBatch() { return daysPerBatch; }
    public void setDaysPerBatch(BigDecimal daysPerBatch) { this.daysPerBatch = daysPerBatch; }

    public BigDecimal getMarginLowerPct() { return marginLowerPct; }
    public void setMarginLowerPct(BigDecimal marginLowerPct) { this.marginLowerPct = marginLowerPct; }

    public BigDecimal getMarginUpperPct() { return marginUpperPct; }
    public void setMarginUpperPct(BigDecimal marginUpperPct) { this.marginUpperPct = marginUpperPct; }

    public BigDecimal getCachedTotalDirect() { return cachedTotalDirect; }
    public void setCachedTotalDirect(BigDecimal cachedTotalDirect) { this.cachedTotalDirect = cachedTotalDirect; }

    public BigDecimal getCachedUnitCost() { return cachedUnitCost; }
    public void setCachedUnitCost(BigDecimal cachedUnitCost) { this.cachedUnitCost = cachedUnitCost; }

    public BigDecimal getCachedPriceLower() { return cachedPriceLower; }
    public void setCachedPriceLower(BigDecimal cachedPriceLower) { this.cachedPriceLower = cachedPriceLower; }

    public BigDecimal getCachedPriceUpper() { return cachedPriceUpper; }
    public void setCachedPriceUpper(BigDecimal cachedPriceUpper) { this.cachedPriceUpper = cachedPriceUpper; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
