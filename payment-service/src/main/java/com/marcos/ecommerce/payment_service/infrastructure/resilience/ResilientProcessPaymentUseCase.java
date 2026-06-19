package com.marcos.ecommerce.payment_service.infrastructure.resilience;

import com.marcos.ecommerce.payment_service.application.usecase.ProcessPaymentUseCase;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.function.Supplier;

public class ResilientProcessPaymentUseCase {

    private static final Logger log = LoggerFactory.getLogger(ResilientProcessPaymentUseCase.class);

    private final ProcessPaymentUseCase delegate;
    private final Retry retry;
    private final CircuitBreaker circuitBreaker;

    public ResilientProcessPaymentUseCase(
            ProcessPaymentUseCase delegate,
            Retry retry,
            CircuitBreaker circuitBreaker
    ) {
        this.delegate = delegate;
        this.retry = retry;
        this.circuitBreaker = circuitBreaker;
    }

    public boolean execute(Long orderId, BigDecimal amount) {
        Supplier<Boolean> decorated = CircuitBreaker.decorateSupplier(
                circuitBreaker,
                Retry.decorateSupplier(retry, () -> delegate.execute(orderId, amount))
        );

        try {
            return decorated.get();
        } catch (Exception e) {
            log.error("Payment failed after retries: orderId={}, reason={}", orderId, e.getMessage());
            return false;
        }
    }
}