package com.example.poc.fee;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
public class FeeCalculationController {

    private final FeeCalculationService feeCalculationService;

    public FeeCalculationController(FeeCalculationService feeCalculationService) {
        this.feeCalculationService = feeCalculationService;
    }

    @PostMapping("/calculate-fee")
    public ResponseEntity<FeeResult> calculateFee(@Valid @RequestBody FeeCalculationRequest request) {
        FeeResult result = feeCalculationService.calculateFeeAndTotal(
                request.getTransactionAmount(),
                request.getTransactionType()
        );
        return ResponseEntity.ok(result);
    }
}
