package com.costcraft.util.money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public class ChineseYuanStrategy implements CurrencyFormatStrategy {

    private static final BigDecimal RATE_TO_BASE = new BigDecimal("0.000455");

    @Override
    public String format(BigDecimal valueInBase) {
        if (valueInBase == null) return "¥0.00";
        BigDecimal converted = valueInBase.multiply(RATE_TO_BASE).setScale(2, RoundingMode.HALF_UP);
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.CHINA);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return "¥" + nf.format(converted);
    }

    @Override
    public String getCurrencyCode() { return "CNY"; }
}
