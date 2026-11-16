package com.costcraft.dto;

import java.math.BigDecimal;

/** Aggregate totals for the sales report. */
public class SalesSummary {
    private final BigDecimal revenue;
    private final BigDecimal cost;
    private final int orderCount;

    public SalesSummary(BigDecimal revenue, BigDecimal cost, int orderCount) {
        this.revenue = revenue;
        this.cost = cost;
        this.orderCount = orderCount;
    }

    public BigDecimal getRevenue() { return revenue; }
    public BigDecimal getCost() { return cost; }
    public BigDecimal getProfit() { return revenue.subtract(cost); }
    public int getOrderCount() { return orderCount; }
}
