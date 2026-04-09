package com.example.poc.fee.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class FxRateService {

    private static final Logger log = LoggerFactory.getLogger(FxRateService.class);

    private final RestTemplate restTemplate;
    private final Map<String, CachedRate> cache = new ConcurrentHashMap<>();

    @Value("${fx.api.base-url}")
    private String apiBaseUrl;

    @Value("${fx.api.key}")
    private String apiKey;

    @Value("${fx.cache.ttl-minutes}")
    private long cacheTtlMinutes;

    @Value("${fee.spread.percentage}")
    private BigDecimal spreadPercentage;

    public FxRateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public BigDecimal getCustomerRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            return BigDecimal.ONE;
        }

        String from = fromCurrency.toUpperCase();
        String to = toCurrency.toUpperCase();
        String key = from + "_" + to;

        CachedRate cached = cache.get(key);
        if (cached != null && !cached.isExpired()) {
            log.info("FX cache hit for {}: {}", key, cached.getRate());
            return cached.getRate();
        }

        try {
            BigDecimal marketRate = fetchFromApi(from, to);
            BigDecimal customerRate = marketRate
                    .multiply(BigDecimal.ONE.add(spreadPercentage))
                    .setScale(6, RoundingMode.HALF_UP);

            log.info("FX live rate for {}: market={} spread={} customerRate={}", key, marketRate, spreadPercentage, customerRate);

            cache.put(key, new CachedRate(customerRate, Instant.now().plus(cacheTtlMinutes, ChronoUnit.MINUTES)));
            return customerRate;

        } catch (FxRateUnavailableException e) {
            throw e;
        } catch (Exception e) {
            log.error("FX API call failed for {}: {}", key, e.getMessage());
            throw new FxRateUnavailableException("Exchange rate unavailable for " + key + ". Please try again later.");
        }
    }

 
    @SuppressWarnings("unchecked")
    private BigDecimal fetchFromApi(String from, String to) {
        String url = apiBaseUrl + "/" + apiKey + "/pair/" + from + "/" + to;
        log.info("Fetching FX rate from ExchangeRate-API: {}/{}", from, to);

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null || !"success".equals(response.get("result"))) {
            throw new FxRateUnavailableException("Invalid response from FX API for " + from + "_" + to);
        }

        Object rateObj = response.get("conversion_rate");
        if (rateObj == null) {
            throw new FxRateUnavailableException("conversion_rate missing in FX API response for " + from + "_" + to);
        }

        return new BigDecimal(rateObj.toString());
    }

    /**
     * Returns cache status for all cached currency pairs.
     */
    public Map<String, Object> getCacheStatus() {
        Map<String, Object> status = new java.util.LinkedHashMap<>();
        if (cache.isEmpty()) {
            status.put("message", "Cache is empty — no rates fetched yet");
            return status;
        }
        cache.forEach((key, cached) -> {
            status.put(key, Map.of(
                    "rate", cached.getRate(),
                    "expired", cached.isExpired(),
                    "expiresAt", cached.getExpiresAt().toString()
            ));
        });
        return status;
    }
}
