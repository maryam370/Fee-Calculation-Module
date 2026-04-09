package com.example.poc.fee.service;

import com.example.poc.fee.model.FeeCalculationRequest;
import com.example.poc.fee.model.FeeResult;
import com.example.poc.fee.model.TransactionFeeRecord;
import com.example.poc.fee.model.TransactionType;
import com.example.poc.fee.repository.TransactionFeeRepository;
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

    @Value("${fee.spread.percentage}")
    private BigDecimal spreadPercentage;

    private final FxRateService fxRateService;
    private final TransactionFeeRepository repository;

    public FeeCalculationService(FxRateService fxRateService, TransactionFeeRepository repository) {
        this.fxRateService = fxRateService;
        this.repository = repository;
    }

    public FeeResult calculateFeeAndTotal(FeeCalculationRequest request) {
        BigDecimal transactionAmount = request.getTransactionAmount();
        TransactionType transactionType = request.getTransactionType();
        String fromCurrency = request.getFromCurrency().toUpperCase();
        String toCurrency = request.getToCurrency().toUpperCase();

        log.info("Calculating fee: type={} amount={} from={} to={}", transactionType, transactionAmount, fromCurrency, toCurrency);

        BigDecimal principal = transactionAmount.setScale(SCALE, ROUNDING);
        BigDecimal commission;
        BigDecimal exchangeRate;
        BigDecimal appliedSpread;

        switch (transactionType) {
            case P2P:
                exchangeRate = BigDecimal.ONE;
                appliedSpread = BigDecimal.ZERO.setScale(SCALE);
                commission = principal.multiply(feePercentage).setScale(SCALE, ROUNDING);
                break;

            case ME2ME_SAME_CURRENCY:
                exchangeRate = BigDecimal.ONE;
                appliedSpread = BigDecimal.ZERO.setScale(SCALE);
                commission = BigDecimal.ZERO.setScale(SCALE);
                break;

            case ME2ME_CROSS_CURRENCY:
                exchangeRate = fxRateService.getRate(fromCurrency, toCurrency);
                BigDecimal convertedAmount = principal.multiply(exchangeRate).setScale(SCALE, ROUNDING);
                appliedSpread = spreadPercentage;
                commission = convertedAmount.multiply(spreadPercentage).setScale(SCALE, ROUNDING);
                break;

            default:
                throw new IllegalArgumentException("Unsupported transaction type: " + transactionType);
        }

        BigDecimal totalDebit = principal.add(commission);

        log.info("Fee result: principal={} commission={} totalDebit={} rate={} spread={}",
                principal, commission, totalDebit, exchangeRate, appliedSpread);

        // Persist to H2
        TransactionFeeRecord record = new TransactionFeeRecord();
        record.setTransactionAmount(principal);
        record.setFromCurrency(fromCurrency);
        record.setToCurrency(toCurrency);
        record.setExchangeRateUsed(exchangeRate);
        record.setSpreadPercentage(appliedSpread);
        record.setCommissionAmount(commission);
        record.setTotalDebit(totalDebit);
        record.setTransactionType(transactionType);
        repository.save(record);

        return new FeeResult(principal, commission, totalDebit, fromCurrency, toCurrency, exchangeRate, appliedSpread);
    }
}
