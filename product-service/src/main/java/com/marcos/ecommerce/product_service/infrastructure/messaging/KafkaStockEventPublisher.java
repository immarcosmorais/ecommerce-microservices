package com.marcos.ecommerce.product_service.infrastructure.messaging;

import com.marcos.ecommerce.product_service.application.messaging.event.StockReservedEvent;
import com.marcos.ecommerce.product_service.application.port.StockEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaStockEventPublisher implements StockEventPublisher {

    private static final String TOPIC = "stock-reserved";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaStockEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void stockReserved(Long orderId) {
        this.kafkaTemplate.send(TOPIC, String.valueOf(orderId), new StockReservedEvent(orderId));
    }
}
