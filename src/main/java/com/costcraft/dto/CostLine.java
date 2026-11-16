package com.costcraft.dto;

import java.math.BigDecimal;

public class CostLine {
    private String label;
    private String stage;
    private BigDecimal amount;
    private String description;

    public CostLine(String label, String stage, BigDecimal amount, String description) {
        this.label = label;
        this.stage = stage;
        this.amount = amount;
        this.description = description;
    }

    public String getLabel() { return label; }
    public String getStage() { return stage; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
}
