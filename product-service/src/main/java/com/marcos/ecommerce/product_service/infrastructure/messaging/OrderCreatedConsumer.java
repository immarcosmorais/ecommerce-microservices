package com.marcos.ecommerce.product_service.infrastructure.messaging;

import com.marcos.ecommerce.product_service.application.usecase.DecreaseStockUseCase;
import com.marcos.ecommerce.product_service.domain.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedConsumer.class);
    private final DecreaseStockUseCase decreaseStockUseCase;

    public OrderCreatedConsumer(DecreaseStockUseCase decreaseStockUseCase) {
        this.decreaseStockUseCase = decreaseStockUseCase;
    }

    @KafkaListener(topics = "order-created", groupId = "product-group")
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent — updating stock for orderId: {}", event.orderId());
        event.items().forEach(item -> {
            log.info("Decreasing stock: productId={}, quantity={}", item.productId(), item.quantity());
            decreaseStockUseCase.execute(item.productId(), item.quantity());
        });
        log.info("Stock updated successfully for orderId: {}", event.orderId());
    }

}
