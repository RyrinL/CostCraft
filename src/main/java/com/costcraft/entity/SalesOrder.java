package com.costcraft.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

/** An order (for an event/festival/customer) made up of one or more product lines. */
public class SalesOrder {
    private Long id;
    private String customerName;
    private LocalDate orderDate;
    private String note;
    private BigDecimal recordedRevenue = BigDecimal.ZERO;
    private BigDecimal recordedCost = BigDecimal.ZERO;
    private Long userId;
    private String username;
    private int itemCount;       // populated by list queries
    private Date createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public BigDecimal getRecordedRevenue() { return recordedRevenue; }
    public void setRecordedRevenue(BigDecimal recordedRevenue) { this.recordedRevenue = recordedRevenue; }

    public BigDecimal getRecordedCost() { return recordedCost; }
    public void setRecordedCost(BigDecimal recordedCost) { this.recordedCost = recordedCost; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    /** Profit = revenue − cost (base currency). */
    public BigDecimal getProfit() { return recordedRevenue.subtract(recordedCost); }
}
