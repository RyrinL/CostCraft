package com.costcraft.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Material {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private BigDecimal packageSize;
    private BigDecimal packagePrice;
    private BigDecimal unitCost;
    private String unit;
    private BigDecimal currentStock;
    private BigDecimal lowStockThreshold;
    private LocalDateTime createdAt;

    public Material() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPackageSize() { return packageSize; }
    public void setPackageSize(BigDecimal packageSize) { this.packageSize = packageSize; }

    public BigDecimal getPackagePrice() { return packagePrice; }
    public void setPackagePrice(BigDecimal packagePrice) { this.packagePrice = packagePrice; }

    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public BigDecimal getCurrentStock() { return currentStock; }
    public void setCurrentStock(BigDecimal currentStock) { this.currentStock = currentStock; }

    public BigDecimal getLowStockThreshold() { return lowStockThreshold; }
    public void setLowStockThreshold(BigDecimal lowStockThreshold) { this.lowStockThreshold = lowStockThreshold; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isBelowThreshold() {
        if (currentStock == null || lowStockThreshold == null) return false;
        return currentStock.compareTo(lowStockThreshold) <= 0;
    }
}
