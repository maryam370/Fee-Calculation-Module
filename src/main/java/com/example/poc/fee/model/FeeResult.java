package com.example.poc.fee.model;

import java.math.BigDecimal;

public class FeeResult {

    private BigDecimal principalAmount;
    private BigDecimal commissionAmount;
    private BigDecimal totalDebit;

    public FeeResult(BigDecimal principalAmount, BigDecimal commissionAmount, BigDecimal totalDebit) {
        this.principalAmount = principalAmount;
        this.commissionAmount = commissionAmount;
        this.totalDebit = totalDebit;
    }

    public BigDecimal getPrincipalAmount() { return principalAmount; }
    public BigDecimal getCommissionAmount() { return commissionAmount; }
    public BigDecimal getTotalDebit() { return totalDebit; }
}
