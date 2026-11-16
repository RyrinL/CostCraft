package com.costcraft.dto;

import java.math.BigDecimal;

/**
 * One line of the shopping list: a material that needs buying, either because it
 * is below its low-stock threshold or because planned production requires more
 * than is currently in stock.
 */
public class ShoppingItem {
    private final String materialName;
    private final String unit;
    private final BigDecimal have;          // current stock
    private final BigDecimal threshold;     // low-stock threshold
    private final BigDecimal needForPlan;   // grams required by planned batches (0 if none)
    private final BigDecimal toBuy;         // recommended purchase quantity
    private final BigDecimal estCost;       // toBuy * unit cost, in base currency (IDR)
    private final boolean belowThreshold;

    public ShoppingItem(String materialName, String unit, BigDecimal have, BigDecimal threshold,
                        BigDecimal needForPlan, BigDecimal toBuy, BigDecimal estCost, boolean belowThreshold) {
        this.materialName = materialName;
        this.unit = unit;
        this.have = have;
        this.threshold = threshold;
        this.needForPlan = needForPlan;
        this.toBuy = toBuy;
        this.estCost = estCost;
        this.belowThreshold = belowThreshold;
    }

    public String getMaterialName() { return materialName; }
    public String getUnit() { return unit; }
    public BigDecimal getHave() { return have; }
    public BigDecimal getThreshold() { return threshold; }
    public BigDecimal getNeedForPlan() { return needForPlan; }
    public BigDecimal getToBuy() { return toBuy; }
    public BigDecimal getEstCost() { return estCost; }
    public boolean isBelowThreshold() { return belowThreshold; }
}
