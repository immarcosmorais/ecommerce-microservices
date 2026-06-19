package com.marcos.ecommerce.payment_service.infrastructure.config;

import com.marcos.ecommerce.payment_service.application.usecase.ProcessPaymentUseCase;
import com.marcos.ecommerce.payment_service.infrastructure.resilience.ResilientProcessPaymentUseCase;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public ProcessPaymentUseCase processPaymentUseCase() {
        return new ProcessPaymentUseCase();
    }

    @Bean
    public ResilientProcessPaymentUseCase resilientProcessPaymentUseCase(
            ProcessPaymentUseCase processPaymentUseCase,
            Retry paymentRetry,
            CircuitBreaker paymentCircuitBreaker
    ) {
        return new ResilientProcessPaymentUseCase(
                processPaymentUseCase,
                paymentRetry,
                paymentCircuitBreaker
        );
    }
}