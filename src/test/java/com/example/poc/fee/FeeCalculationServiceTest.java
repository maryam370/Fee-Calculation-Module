package com.example.poc.fee;

import com.example.poc.fee.model.FeeResult;
import com.example.poc.fee.model.TransactionType;
import com.example.poc.fee.service.FeeCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class FeeCalculationServiceTest {

    private FeeCalculationService service;

    @BeforeEach
    void setUp() {
        service = new FeeCalculationService();
        ReflectionTestUtils.setField(service, "feePercentage", new BigDecimal("0.01"));
    }

    @Test
    void p2p_shouldApplyFee() {
        FeeResult result = service.calculateFeeAndTotal(new BigDecimal("100.000"), TransactionType.P2P);

        assertEquals(new BigDecimal("100.000"), result.getPrincipalAmount());
        assertEquals(new BigDecimal("1.000"), result.getCommissionAmount());
        assertEquals(new BigDecimal("101.000"), result.getTotalDebit());
    }

    @Test
    void me2me_sameCurrency_shouldBeFeeFree() {
        FeeResult result = service.calculateFeeAndTotal(new BigDecimal("250.000"), TransactionType.ME2ME_SAME_CURRENCY);

        assertEquals(new BigDecimal("0.000"), result.getCommissionAmount());
        assertEquals(new BigDecimal("250.000"), result.getTotalDebit());
    }

    @Test
    void me2me_crossCurrency_shouldBeFeeFree() {
        FeeResult result = service.calculateFeeAndTotal(new BigDecimal("200.000"), TransactionType.ME2ME_CROSS_CURRENCY);

        assertEquals(new BigDecimal("0.000"), result.getCommissionAmount());
        assertEquals(new BigDecimal("200.000"), result.getTotalDebit());
    }

    @Test
    void p2p_roundingPrecision_shouldRoundHalfUp() {
        FeeResult result = service.calculateFeeAndTotal(new BigDecimal("33.333"), TransactionType.P2P);

        assertEquals(new BigDecimal("0.333"), result.getCommissionAmount());
    }

    @Test
    void nullTransactionType_shouldThrowException() {
        assertThrows(NullPointerException.class, () ->
                service.calculateFeeAndTotal(new BigDecimal("100.000"), null)
        );
    }
}
