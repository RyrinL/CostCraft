package com.costcraft.entity;

import java.math.BigDecimal;

public class BomItem {
    private Long id;
    private Long productId;
    private Long materialId;
    private String materialName;
    private String materialUnit;
    private BigDecimal gramQuantity;
    private BigDecimal materialUnitCost;
    private int displayOrder;

    public BomItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }

    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }

    public String getMaterialUnit() { return materialUnit; }
    public void setMaterialUnit(String materialUnit) { this.materialUnit = materialUnit; }

    public BigDecimal getGramQuantity() { return gramQuantity; }
    public void setGramQuantity(BigDecimal gramQuantity) { this.gramQuantity = gramQuantity; }

    public BigDecimal getMaterialUnitCost() { return materialUnitCost; }
    public void setMaterialUnitCost(BigDecimal materialUnitCost) { this.materialUnitCost = materialUnitCost; }

    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }

    public BigDecimal getLineCost() {
        if (gramQuantity == null || materialUnitCost == null) return BigDecimal.ZERO;
        return gramQuantity.multiply(materialUnitCost);
    }
}
