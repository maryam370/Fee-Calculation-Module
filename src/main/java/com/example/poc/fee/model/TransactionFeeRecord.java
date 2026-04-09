package com.example.poc.fee.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_fee_record")
public class TransactionFeeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal transactionAmount;
    private String fromCurrency;
    private String toCurrency;
    private BigDecimal exchangeRateUsed;
    private BigDecimal spreadPercentage;
    private BigDecimal commissionAmount;
    private BigDecimal totalDebit;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private LocalDateTime timestamp;

    // Hardcoded for POC — will come from auth context in production
    private String userId = "poc-user-001";

    @PrePersist
    public void prePersist() {
        this.timestamp = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() { return id; }

    public BigDecimal getTransactionAmount() { return transactionAmount; }
    public void setTransactionAmount(BigDecimal transactionAmount) { this.transactionAmount = transactionAmount; }

    public String getFromCurrency() { return fromCurrency; }
    public void setFromCurrency(String fromCurrency) { this.fromCurrency = fromCurrency; }

    public String getToCurrency() { return toCurrency; }
    public void setToCurrency(String toCurrency) { this.toCurrency = toCurrency; }

    public BigDecimal getExchangeRateUsed() { return exchangeRateUsed; }
    public void setExchangeRateUsed(BigDecimal exchangeRateUsed) { this.exchangeRateUsed = exchangeRateUsed; }

    public BigDecimal getSpreadPercentage() { return spreadPercentage; }
    public void setSpreadPercentage(BigDecimal spreadPercentage) { this.spreadPercentage = spreadPercentage; }

    public BigDecimal getCommissionAmount() { return commissionAmount; }
    public void setCommissionAmount(BigDecimal commissionAmount) { this.commissionAmount = commissionAmount; }

    public BigDecimal getTotalDebit() { return totalDebit; }
    public void setTotalDebit(BigDecimal totalDebit) { this.totalDebit = totalDebit; }

    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }

    public LocalDateTime getTimestamp() { return timestamp; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
