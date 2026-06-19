package com.marcos.ecommerce.payment_service.application.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

public class ProcessPaymentUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessPaymentUseCase.class);
    private final AtomicInteger attemptCounter = new AtomicInteger(0);

    public boolean execute(Long orderId, BigDecimal amount) {
        if (amount.compareTo(new BigDecimal("500")) >= 0) {
            log.info("Payment REJECTED by business rule: orderId={}, amount={}", orderId, amount);
            return false;
        }

        int attempt = attemptCounter.incrementAndGet();
        log.info("Calling payment gateway: orderId={}, attempt={}", orderId, attempt);
        if (attempt <= 2) {
            throw new RuntimeException(
                    "Falha transitória no gateway (tentativa " + attempt + ")");
        }

        attemptCounter.set(0);
        log.info("Payment APPROVED: orderId={}", orderId);
        return true;
    }
}