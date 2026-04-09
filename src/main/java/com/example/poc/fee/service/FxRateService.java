package com.example.poc.fee.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;


@Service
public class FxRateService {

    // Hardcoded rates: key = "FROM_TO", value = rate
    private static final Map<String, BigDecimal> RATES = Map.of(
            "USD_BHD", new BigDecimal("0.377"),
            "BHD_USD", new BigDecimal("2.653"),
            "USD_EUR", new BigDecimal("0.921"),
            "EUR_USD", new BigDecimal("1.086"),
            "BHD_EUR", new BigDecimal("0.347"),
            "EUR_BHD", new BigDecimal("0.408"),
            "USD_GBP", new BigDecimal("0.786"),
            "GBP_USD", new BigDecimal("1.272")
    );

    /**
     * Returns the exchange rate for the given currency pair.
     * Same currency returns 1.
     *
     * @throws IllegalArgumentException if the pair is not supported
     */
    public BigDecimal getRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            return BigDecimal.ONE;
        }
        String key = fromCurrency.toUpperCase() + "_" + toCurrency.toUpperCase();
        BigDecimal rate = RATES.get(key);
        if (rate == null) {
            throw new IllegalArgumentException("Unsupported currency pair: " + key);
        }
        return rate;
    }
}
