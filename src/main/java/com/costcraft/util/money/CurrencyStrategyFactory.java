package com.costcraft.util.money;


public class CurrencyStrategyFactory {

    private CurrencyStrategyFactory() {}

    public static CurrencyFormatStrategy forCurrency(String currencyCode) {
        if (currencyCode == null) return new IndonesianRupiahStrategy();
        return switch (currencyCode.toUpperCase()) {
            case "USD" -> new USDollarStrategy();
            case "CNY" -> new ChineseYuanStrategy();
            case "EUR" -> new EuroStrategy();
            default   -> new IndonesianRupiahStrategy();
        };
    }
}
