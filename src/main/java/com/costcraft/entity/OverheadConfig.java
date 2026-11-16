package com.costcraft.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OverheadConfig {
    private Long id;
    private String name;
    private String costStage;       // DIRECT_OTHER or PERCENTAGE_OVERHEAD
    private String allocationRule;  // PER_BATCH_FIXED, PER_UNIT_FIXED, DAILY_ALLOCATED, PERCENTAGE_OF_DIRECT
    private BigDecimal fixedAmount;
    private BigDecimal dailyAmount;
    private int daysPerMonth;
    private BigDecimal percentage;
    private boolean active;
    private int displayOrder;
    private LocalDateTime createdAt;

    public OverheadConfig() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCostStage() { return costStage; }
    public void setCostStage(String costStage) { this.costStage = costStage; }

    public String getAllocationRule() { return allocationRule; }
    public void setAllocationRule(String allocationRule) { this.allocationRule = allocationRule; }

    public BigDecimal getFixedAmount() { return fixedAmount; }
    public void setFixedAmount(BigDecimal fixedAmount) { this.fixedAmount = fixedAmount; }

    public BigDecimal getDailyAmount() { return dailyAmount; }
    public void setDailyAmount(BigDecimal dailyAmount) { this.dailyAmount = dailyAmount; }

    public int getDaysPerMonth() { return daysPerMonth; }
    public void setDaysPerMonth(int daysPerMonth) { this.daysPerMonth = daysPerMonth; }

    public BigDecimal getPercentage() { return percentage; }
    public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
