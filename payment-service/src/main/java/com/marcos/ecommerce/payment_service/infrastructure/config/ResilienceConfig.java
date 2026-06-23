package com.marcos.ecommerce.payment_service.infrastructure.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ResilienceConfig {

    private static final Logger log = LoggerFactory.getLogger(ResilienceConfig.class);

    @Bean
    public Retry paymentRetry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(500))
                .retryExceptions(RuntimeException.class)
                .build();

        Retry retry = Retry.of("paymentRetry", config);

        retry.getEventPublisher()
                .onRetry(event -> log.warn(
                        "Retry attempt #{} for orderId — reason: {}",
                        event.getNumberOfRetryAttempts(),
                        event.getLastThrowable().getMessage()))
                .onSuccess(event -> log.info(
                        "Retry succeeded after {} attempt(s)",
                        event.getNumberOfRetryAttempts()))
                .onError(event -> log.error(
                        "All {} retry attempts exhausted",
                        event.getNumberOfRetryAttempts()));

        return retry;
    }

    @Bean
    public CircuitBreaker paymentCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowSize(5)
                .failureRateThreshold(60)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .permittedNumberOfCallsInHalfOpenState(2)
                .build();

        CircuitBreaker cb = CircuitBreaker.of("paymentCircuitBreaker", config);

        cb.getEventPublisher()
                .onStateTransition(event -> log.warn(
                        "Circuit Breaker state: {} → {}",
                        event.getStateTransition().getFromState(),
                        event.getStateTransition().getToState()))
                .onCallNotPermitted(event -> log.error(
                        "Circuit Breaker OPEN — call rejected immediately"))
                .onFailureRateExceeded(event -> log.error(
                        "Failure rate exceeded: {}%", event.getFailureRate()));

        return cb;
    }
}