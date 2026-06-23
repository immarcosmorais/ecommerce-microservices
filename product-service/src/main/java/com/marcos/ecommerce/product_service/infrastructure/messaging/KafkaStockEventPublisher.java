package com.marcos.ecommerce.product_service.infrastructure.messaging;

import com.marcos.ecommerce.product_service.application.messaging.event.StockReservedEvent;
import com.marcos.ecommerce.product_service.application.port.StockEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaStockEventPublisher implements StockEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaStockEventPublisher.class);
    private static final String TOPIC = "stock-reserved";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaStockEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void stockReserved(StockReservedEvent event) {
        log.info("Publishing event to topic: {}, key: {}", TOPIC, event.orderId());
        this.kafkaTemplate.send(TOPIC, String.valueOf(event.orderId()), event);
    }
}
