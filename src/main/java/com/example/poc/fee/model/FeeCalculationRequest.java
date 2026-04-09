package com.example.poc.fee.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class FeeCalculationRequest {

    @NotNull(message = "Transaction amount is required")
    @Positive(message = "Transaction amount must be greater than zero")
    private BigDecimal transactionAmount;

    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;

    @NotNull(message = "From currency is required")
    private String fromCurrency;

    @NotNull(message = "To currency is required")
    private String toCurrency;

    public BigDecimal getTransactionAmount() { return transactionAmount; }
    public void setTransactionAmount(BigDecimal transactionAmount) { this.transactionAmount = transactionAmount; }

    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }

    public String getFromCurrency() { return fromCurrency; }
    public void setFromCurrency(String fromCurrency) { this.fromCurrency = fromCurrency; }

    public String getToCurrency() { return toCurrency; }
    public void setToCurrency(String toCurrency) { this.toCurrency = toCurrency; }
}
