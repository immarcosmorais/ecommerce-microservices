package com.marcos.ecommerce.payment_service.infrastructure.messaging;

import com.marcos.ecommerce.payment_service.domain.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedConsumer.class);

    @KafkaListener(topics = "order-created", groupId = "payment-group")
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("=== PAYMENT SERVICE ===");
        log.info("Processing payment for orderId: {}", event.orderId());
        log.info("Total amount to charge: {}", event.totalAmount());
        log.info("Payment APPROVED for orderId: {}", event.orderId());
    }

}
