package com.costcraft.util;

import com.costcraft.entity.User;
import com.costcraft.util.money.CurrencyStrategyFactory;
import com.costcraft.util.money.CurrencyFormatStrategy;

import java.math.BigDecimal;

public class MoneyFormatter {

    private MoneyFormatter() {}

    public static String format(BigDecimal valueInBase, User user) {
        String currency = (user != null && user.getDisplayCurrency() != null)
                ? user.getDisplayCurrency() : "IDR";
        CurrencyFormatStrategy strategy = CurrencyStrategyFactory.forCurrency(currency);
        return strategy.format(valueInBase);
    }

    public static String format(BigDecimal valueInBase, String currencyCode) {
        CurrencyFormatStrategy strategy = CurrencyStrategyFactory.forCurrency(currencyCode);
        return strategy.format(valueInBase);
    }
}
