package com.costcraft.entity;

import java.math.BigDecimal;
import java.util.Date;

public class ProductionBatch {
    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal multiplier;
    private BigDecimal unitsProduced;
    private BigDecimal recordedUnitCost;
    private BigDecimal recordedTotalCost;
    private Long workerId;
    private String workerUsername;
    private String notes;
    private Date createdAt;

    public ProductionBatch() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getMultiplier() { return multiplier; }
    public void setMultiplier(BigDecimal multiplier) { this.multiplier = multiplier; }

    public BigDecimal getUnitsProduced() { return unitsProduced; }
    public void setUnitsProduced(BigDecimal unitsProduced) { this.unitsProduced = unitsProduced; }

    public BigDecimal getRecordedUnitCost() { return recordedUnitCost; }
    public void setRecordedUnitCost(BigDecimal recordedUnitCost) { this.recordedUnitCost = recordedUnitCost; }

    public BigDecimal getRecordedTotalCost() { return recordedTotalCost; }
    public void setRecordedTotalCost(BigDecimal recordedTotalCost) { this.recordedTotalCost = recordedTotalCost; }

    public Long getWorkerId() { return workerId; }
    public void setWorkerId(Long workerId) { this.workerId = workerId; }

    public String getWorkerUsername() { return workerUsername; }
    public void setWorkerUsername(String workerUsername) { this.workerUsername = workerUsername; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
