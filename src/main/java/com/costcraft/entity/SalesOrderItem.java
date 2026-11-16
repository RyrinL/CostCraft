package com.costcraft.entity;

import java.math.BigDecimal;

/** A single product line within a sales order. */
public class SalesOrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private BigDecimal quantity;
    private BigDecimal unitPrice;          // price charged per unit
    private BigDecimal unitCostSnapshot;   // product HPP at sale time (historical)
    private BigDecimal lineRevenue;
    private BigDecimal lineCost;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getUnitCostSnapshot() { return unitCostSnapshot; }
    public void setUnitCostSnapshot(BigDecimal unitCostSnapshot) { this.unitCostSnapshot = unitCostSnapshot; }

    public BigDecimal getLineRevenue() { return lineRevenue; }
    public void setLineRevenue(BigDecimal lineRevenue) { this.lineRevenue = lineRevenue; }

    public BigDecimal getLineCost() { return lineCost; }
    public void setLineCost(BigDecimal lineCost) { this.lineCost = lineCost; }

    public BigDecimal getLineProfit() { return lineRevenue.subtract(lineCost); }
}
