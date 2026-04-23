package com.marcos.ecommerce.order_service.infrastructure.messaging;

import com.marcos.ecommerce.order_service.domain.event.OrderCreatedEvent;
import com.marcos.ecommerce.order_service.domain.event.OrderEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaOrderEventPublisher implements OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaOrderEventPublisher.class);
    private static final String TOPIC = "order-created";
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public KafkaOrderEventPublisher(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publisherOrderCreated(OrderCreatedEvent event) {
        log.info("Publishing OrderCreatedEvent for orderId: {}", event.orderId());
        this.kafkaTemplate.send(TOPIC, String.valueOf(event.orderId()), event);
        log.info("Event published successfully to topic {}", TOPIC);
    }
}
