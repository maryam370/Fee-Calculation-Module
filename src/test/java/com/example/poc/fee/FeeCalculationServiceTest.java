package com.example.poc.fee;

import com.example.poc.fee.model.FeeCalculationRequest;
import com.example.poc.fee.model.FeeResult;
import com.example.poc.fee.model.TransactionType;
import com.example.poc.fee.repository.TransactionFeeRepository;
import com.example.poc.fee.service.FeeCalculationService;
import com.example.poc.fee.service.FxRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeeCalculationServiceTest {

    private FeeCalculationService service;
    private TransactionFeeRepository repository;

    @BeforeEach
    void setUp() {
        repository = mock(TransactionFeeRepository.class);
        service = new FeeCalculationService(new FxRateService(), repository);
        ReflectionTestUtils.setField(service, "feePercentage", new BigDecimal("0.01"));
        ReflectionTestUtils.setField(service, "spreadPercentage", new BigDecimal("0.005"));
    }

    private FeeCalculationRequest request(BigDecimal amount, TransactionType type, String from, String to) {
        FeeCalculationRequest req = new FeeCalculationRequest();
        req.setTransactionAmount(amount);
        req.setTransactionType(type);
        req.setFromCurrency(from);
        req.setToCurrency(to);
        return req;
    }

    @Test
    void p2p_shouldApplyFee() {
        FeeResult result = service.calculateFeeAndTotal(request(new BigDecimal("100.000"), TransactionType.P2P, "BHD", "BHD"));

        assertEquals(new BigDecimal("100.000"), result.getPrincipalAmount());
        assertEquals(new BigDecimal("1.000"), result.getCommissionAmount());
        assertEquals(new BigDecimal("101.000"), result.getTotalDebit());
    }

    @Test
    void me2me_sameCurrency_shouldBeFeeFree() {
        FeeResult result = service.calculateFeeAndTotal(request(new BigDecimal("250.000"), TransactionType.ME2ME_SAME_CURRENCY, "BHD", "BHD"));

        assertEquals(new BigDecimal("0.000"), result.getCommissionAmount());
        assertEquals(new BigDecimal("250.000"), result.getTotalDebit());
    }

    @Test
    void me2me_crossCurrency_shouldApplySpread() {
        // 100 USD → BHD at rate 0.377 = 37.700 converted, spread = 37.700 * 0.005 = 0.189
        FeeResult result = service.calculateFeeAndTotal(request(new BigDecimal("100.000"), TransactionType.ME2ME_CROSS_CURRENCY, "USD", "BHD"));

        assertEquals(new BigDecimal("0.377"), result.getExchangeRateUsed());
        assertEquals(new BigDecimal("0.189"), result.getCommissionAmount());
        assertEquals(new BigDecimal("100.189"), result.getTotalDebit());
    }

    @Test
    void p2p_roundingPrecision_shouldRoundHalfUp() {
        FeeResult result = service.calculateFeeAndTotal(request(new BigDecimal("33.333"), TransactionType.P2P, "BHD", "BHD"));

        assertEquals(new BigDecimal("0.333"), result.getCommissionAmount());
    }

    @Test
    void unsupportedCurrencyPair_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () ->
                service.calculateFeeAndTotal(request(new BigDecimal("100.000"), TransactionType.ME2ME_CROSS_CURRENCY, "XYZ", "ABC"))
        );
    }
}
