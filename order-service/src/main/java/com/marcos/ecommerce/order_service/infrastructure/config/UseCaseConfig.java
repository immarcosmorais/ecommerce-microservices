package com.marcos.ecommerce.order_service.infrastructure.config;

import com.marcos.ecommerce.order_service.application.usecase.CancelOrderUseCase;
import com.marcos.ecommerce.order_service.application.usecase.CreateOrderUseCase;
import com.marcos.ecommerce.order_service.application.usecase.GetOrderUseCase;
import com.marcos.ecommerce.order_service.domain.event.OrderEventPublisher;
import com.marcos.ecommerce.order_service.domain.repository.OrderRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateOrderUseCase createOrderUseCase(OrderRepository repository, OrderEventPublisher eventPublisher) {
        return new CreateOrderUseCase(repository, eventPublisher);
    }

    @Bean
    public GetOrderUseCase getOrderUseCase(OrderRepository repository) {
        return new GetOrderUseCase(repository);
    }

    @Bean
    public CancelOrderUseCase cancelOrderUseCase(OrderRepository repository) {
        return new CancelOrderUseCase(repository);
    }

}
