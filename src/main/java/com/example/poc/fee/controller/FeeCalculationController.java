package com.example.poc.fee.controller;

import com.example.poc.fee.model.FeeCalculationRequest;
import com.example.poc.fee.model.FeeResult;
import com.example.poc.fee.service.FeeCalculationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/transfers")
public class FeeCalculationController {

    private final FeeCalculationService feeCalculationService;

    @Value("${fee.percentage}")
    private BigDecimal feePercentage;

    @Value("${fee.spread.percentage}")
    private BigDecimal spreadPercentage;

    public FeeCalculationController(FeeCalculationService feeCalculationService) {
        this.feeCalculationService = feeCalculationService;
    }

    // GET /api/transfers/fee-config — returns the admin-configured fee and spread percentages
    @GetMapping("/fee-config")
    public ResponseEntity<Map<String, Object>> getFeeConfig() {
        return ResponseEntity.ok(Map.of(
                "feePercentage", feePercentage,
                "displayFeePercentage", feePercentage.multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString() + "%",
                "spreadPercentage", spreadPercentage,
                "displaySpreadPercentage", spreadPercentage.multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString() + "%"
        ));
    }

    @PostMapping("/calculate-fee")
    public ResponseEntity<FeeResult> calculateFee(@Valid @RequestBody FeeCalculationRequest request) {
        FeeResult result = feeCalculationService.calculateFeeAndTotal(request);
        return ResponseEntity.ok(result);
    }
}
