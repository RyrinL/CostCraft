package com.costcraft.util.money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public class IndonesianRupiahStrategy implements CurrencyFormatStrategy {

    @Override
    public String format(BigDecimal valueInBase) {
        if (valueInBase == null) return "Rp 0";
        BigDecimal rounded = valueInBase.setScale(0, RoundingMode.HALF_UP);
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.of("id", "ID"));
        nf.setMaximumFractionDigits(0);
        return "Rp " + nf.format(rounded);
    }

    @Override
    public String getCurrencyCode() { return "IDR"; }
}
