package com.costcraft.entity;

import java.math.BigDecimal;

public class BatchItem {
    private Long id;
    private Long batchId;
    private Long materialId;
    private String materialName;
    private BigDecimal gramQuantity;
    private BigDecimal unitCostSnapshot;
    private BigDecimal lineCost;
    private boolean overridden;

    public BatchItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }

    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }

    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }

    public BigDecimal getGramQuantity() { return gramQuantity; }
    public void setGramQuantity(BigDecimal gramQuantity) { this.gramQuantity = gramQuantity; }

    public BigDecimal getUnitCostSnapshot() { return unitCostSnapshot; }
    public void setUnitCostSnapshot(BigDecimal unitCostSnapshot) { this.unitCostSnapshot = unitCostSnapshot; }

    public BigDecimal getLineCost() { return lineCost; }
    public void setLineCost(BigDecimal lineCost) { this.lineCost = lineCost; }

    public boolean isOverridden() { return overridden; }
    public void setOverridden(boolean overridden) { this.overridden = overridden; }
}
