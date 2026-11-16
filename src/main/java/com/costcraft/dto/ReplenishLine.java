package com.costcraft.dto;

import java.math.BigDecimal;

public class ReplenishLine {
    private final String materialName;
    private final String unit;
    private final BigDecimal required;
    private final BigDecimal available;
    private final boolean sufficient;

    public ReplenishLine(String materialName, String unit, BigDecimal required, BigDecimal available) {
        this.materialName = materialName;
        this.unit = unit;
        this.required = required;
        this.available = available;
        this.sufficient = available.compareTo(required) >= 0;
    }

    // EL resolves properties via getters, not fields — required for ${l.sufficient} etc.
    public String getMaterialName() { return materialName; }
    public String getUnit() { return unit; }
    public BigDecimal getRequired() { return required; }
    public BigDecimal getAvailable() { return available; }
    public boolean isSufficient() { return sufficient; }
}
