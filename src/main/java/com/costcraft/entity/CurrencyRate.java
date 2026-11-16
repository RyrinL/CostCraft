package com.costcraft.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CurrencyRate {
    private String code;
    private String name;
    private BigDecimal rateToBase;
    private int decimalPlaces;
    private String symbol;
    private LocalDateTime updatedAt;

    public CurrencyRate() {}

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getRateToBase() { return rateToBase; }
    public void setRateToBase(BigDecimal rateToBase) { this.rateToBase = rateToBase; }

    public int getDecimalPlaces() { return decimalPlaces; }
    public void setDecimalPlaces(int decimalPlaces) { this.decimalPlaces = decimalPlaces; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
