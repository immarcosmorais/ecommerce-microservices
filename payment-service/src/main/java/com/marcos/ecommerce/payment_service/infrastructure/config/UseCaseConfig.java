package com.marcos.ecommerce.payment_service.infrastructure.config;

import com.marcos.ecommerce.payment_service.application.usecase.ProcessPaymentUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public ProcessPaymentUseCase processPaymentUseCase() {
        return new ProcessPaymentUseCase();
    }

}
