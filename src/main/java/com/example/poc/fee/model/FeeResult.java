package com.example.poc.fee.model;

import java.math.BigDecimal;

public class FeeResult {

    private BigDecimal principalAmount;
    private BigDecimal commissionAmount;
    private BigDecimal totalDebit;
    private String fromCurrency;
    private String toCurrency;
    private BigDecimal exchangeRateUsed;
    private BigDecimal spreadPercentage;

    public FeeResult(BigDecimal principalAmount, BigDecimal commissionAmount, BigDecimal totalDebit,
                     String fromCurrency, String toCurrency,
                     BigDecimal exchangeRateUsed, BigDecimal spreadPercentage) {
        this.principalAmount = principalAmount;
        this.commissionAmount = commissionAmount;
        this.totalDebit = totalDebit;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.exchangeRateUsed = exchangeRateUsed;
        this.spreadPercentage = spreadPercentage;
    }

    public BigDecimal getPrincipalAmount() { return principalAmount; }
    public BigDecimal getCommissionAmount() { return commissionAmount; }
    public BigDecimal getTotalDebit() { return totalDebit; }
    public String getFromCurrency() { return fromCurrency; }
    public String getToCurrency() { return toCurrency; }
    public BigDecimal getExchangeRateUsed() { return exchangeRateUsed; }
    public BigDecimal getSpreadPercentage() { return spreadPercentage; }
}
