package com.marcos.ecommerce.order_service.domain.event;

public interface OrderEventPublisher {
    void publisherOrderCreated(OrderCreatedEvent event);
}
