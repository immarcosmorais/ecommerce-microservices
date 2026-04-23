package com.marcos.ecommerce.notification_service.infrastructure.messaging;

import com.marcos.ecommerce.notification_service.domain.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedConsumer.class);

    @KafkaListener(topics = "order-created", groupId = "notification-group")
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("=== NOTIFICATION SERVICE ===");
        log.info("New order received — orderId: {}, customerId: {}",
                event.orderId(), event.customerId());
        log.info("Total amount: {} | Items: {}",
                event.totalAmount(), event.items().size());
        log.info("Sending confirmation email to customer {}...", event.customerId());
        log.info("Email sent successfully for orderId: {}", event.orderId());
    }

}
