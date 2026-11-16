package com.costcraft.service.cost;

import com.costcraft.dto.BatchContext;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DailyAllocatedStrategy implements OverheadAllocationStrategy {

    private final BigDecimal dailyAmount;
    private final int daysPerMonth;
    private final String name;

    public DailyAllocatedStrategy(BigDecimal dailyAmount, int daysPerMonth, String name) {
        this.dailyAmount = dailyAmount;
        this.daysPerMonth = daysPerMonth;
        this.name = name;
    }

    @Override
    public BigDecimal allocate(BatchContext ctx) {
        if (dailyAmount == null || ctx.getDaysPerBatch() == null) return BigDecimal.ZERO;
        BigDecimal days = BigDecimal.valueOf(daysPerMonth > 0 ? daysPerMonth : 30);
        // daily_amount / days_per_month × product.days_per_batch
        return dailyAmount.divide(days, 10, RoundingMode.HALF_UP)
                .multiply(ctx.getDaysPerBatch())
                .setScale(4, RoundingMode.HALF_UP);
    }

    @Override
    public String describe() { return name + ": " + dailyAmount + "/" + daysPerMonth + "days × days_per_batch"; }
}
