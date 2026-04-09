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
import com.example.poc.fee.service.FxRateUnavailableException;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class FeeCalculationServiceTest {

    private FeeCalculationService service;
    private TransactionFeeRepository repository;

    @BeforeEach
    void setUp() {
        repository = mock(TransactionFeeRepository.class);
        FxRateService fxRateService = mock(FxRateService.class);
        service = new FeeCalculationService(fxRateService, repository);
        ReflectionTestUtils.setField(service, "feePercentage", new BigDecimal("0.01"));
        ReflectionTestUtils.setField(service, "spreadPercentage", new BigDecimal("0.005"));

        // Mock FX responses
        when(fxRateService.getCustomerRate("USD", "BHD")).thenReturn(new BigDecimal("0.379"));
        when(fxRateService.getCustomerRate("BHD", "BHD")).thenReturn(BigDecimal.ONE);
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
        // mocked customer rate = 0.379 (market + spread already applied by FxRateService)
        // commission = 100 * 0.379 * 0.005 = 0.190 (rounded)
        FeeResult result = service.calculateFeeAndTotal(request(new BigDecimal("100.000"), TransactionType.ME2ME_CROSS_CURRENCY, "USD", "BHD"));

        assertEquals(new BigDecimal("0.379"), result.getExchangeRateUsed());
        assertEquals(new BigDecimal("0.190"), result.getCommissionAmount());
        assertEquals(new BigDecimal("100.190"), result.getTotalDebit());
    }

    @Test
    void p2p_roundingPrecision_shouldRoundHalfUp() {
        FeeResult result = service.calculateFeeAndTotal(request(new BigDecimal("33.333"), TransactionType.P2P, "BHD", "BHD"));

        assertEquals(new BigDecimal("0.333"), result.getCommissionAmount());
    }

    @Test
    void unsupportedCurrencyPair_shouldThrow() {
        FxRateService fxMock = mock(FxRateService.class);
        when(fxMock.getCustomerRate("XYZ", "ABC")).thenThrow(new FxRateUnavailableException("Exchange rate unavailable for XYZ_ABC"));
        FeeCalculationService s = new FeeCalculationService(fxMock, repository);
        ReflectionTestUtils.setField(s, "feePercentage", new BigDecimal("0.01"));
        ReflectionTestUtils.setField(s, "spreadPercentage", new BigDecimal("0.005"));

        assertThrows(FxRateUnavailableException.class, () ->
                s.calculateFeeAndTotal(request(new BigDecimal("100.000"), TransactionType.ME2ME_CROSS_CURRENCY, "XYZ", "ABC"))
        );
    }
}
