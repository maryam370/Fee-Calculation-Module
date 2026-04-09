package com.example.poc.fee.service;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Holds a cached exchange rate with its expiry timestamp.
 */
class CachedRate {

    private final BigDecimal rate;
    private final Instant expiresAt;

    CachedRate(BigDecimal rate, Instant expiresAt) {
        this.rate = rate;
        this.expiresAt = expiresAt;
    }

    BigDecimal getRate() { return rate; }

    boolean isExpired() { return Instant.now().isAfter(expiresAt); }
}
