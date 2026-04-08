package com.example.poc.fee;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class FeeCalculationService {

    private static final Logger log = LoggerFactory.getLogger(FeeCalculationService.class);
    private static final int SCALE = 3;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

   
    @Value("${fee.percentage}")
    private BigDecimal feePercentage;

    /**
     * Calculates commission and total debit for a given transaction.
     *
     * P2P                  → commission = transactionAmount × feePercentage
     * Me2Me (any currency) → commission = 0 (fee-free per BRD)
     *
     * @param transactionAmount amount the user wants to send
     * @param transactionType   P2P, ME2ME_SAME_CURRENCY, or ME2ME_CROSS_CURRENCY
     * @return FeeResult with principalAmount, commissionAmount, and totalDebit
     */
    public FeeResult calculateFeeAndTotal(BigDecimal transactionAmount, TransactionType transactionType) {
        log.info("Calculating fee for type={} amount={}", transactionType, transactionAmount);

        BigDecimal principal = transactionAmount.setScale(SCALE, ROUNDING);
        BigDecimal commission;

        switch (transactionType) {
            case P2P:
                commission = principal.multiply(feePercentage).setScale(SCALE, ROUNDING);
                break;
            case ME2ME_SAME_CURRENCY:
            case ME2ME_CROSS_CURRENCY:
                commission = BigDecimal.ZERO.setScale(SCALE);
                break;
            default:
                throw new IllegalArgumentException("Unsupported transaction type: " + transactionType);
        }

        BigDecimal totalDebit = principal.add(commission);

        log.info("Fee result: principal={} commission={} totalDebit={}", principal, commission, totalDebit);

        return new FeeResult(principal, commission, totalDebit);
    }
}
