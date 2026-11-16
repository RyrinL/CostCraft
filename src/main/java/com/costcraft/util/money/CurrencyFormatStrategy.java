package com.costcraft.util.money;

import java.math.BigDecimal;

public interface CurrencyFormatStrategy {
    String format(BigDecimal valueInBase);
    String getCurrencyCode();
}
