package com.example.poc.fee.service;

public class FxRateUnavailableException extends RuntimeException {

    public FxRateUnavailableException(String message) {
        super(message);
    }
}
