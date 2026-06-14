package com.marcos.ecommerce.orchestrator_service.infrastructure.config;

import com.marcos.ecommerce.orchestrator_service.application.port.SagaCommandPublisher;
import com.marcos.ecommerce.orchestrator_service.application.service.OrderSagaOrchestrator;
import com.marcos.ecommerce.orchestrator_service.domain.repository.OrderSagaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public OrderSagaOrchestrator orderSagaOrchestrator(
            OrderSagaRepository sagaRepository,
            SagaCommandPublisher publisher
    ) {
        return new OrderSagaOrchestrator(sagaRepository, publisher);
    }
}
