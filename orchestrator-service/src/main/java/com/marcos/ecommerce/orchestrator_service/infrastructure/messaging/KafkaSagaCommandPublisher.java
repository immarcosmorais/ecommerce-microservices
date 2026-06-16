package com.marcos.ecommerce.orchestrator_service.infrastructure.messaging;

import com.marcos.ecommerce.orchestrator_service.application.messaging.command.*;
import com.marcos.ecommerce.orchestrator_service.application.port.SagaCommandPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaSagaCommandPublisher implements SagaCommandPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(KafkaSagaCommandPublisher.class);

    public KafkaSagaCommandPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void reserveStock(ReserveStockCommand c) {
        publish("reserve-stock", String.valueOf(c.orderId()), c);
    }

    @Override
    public void processPayment(ProcessPaymentCommand c) {
        publish("process-payment", String.valueOf(c.orderId()), c);
    }

    @Override
    public void confirmOrder(ConfirmOrderCommand c) {
        publish("confirm-order", String.valueOf(c.orderId()), c);
    }

    @Override
    public void restoreStock(RestoreStockCommand command) {
        publish("restore-stock", String.valueOf(command.orderId()), command);
    }

    @Override
    public void cancelOrder(CancelOrderCommand command) {
        publish("cancel-order", String.valueOf(command.orderId()), command);

    }

    private <T> void publish(String topic, String key, T command) {
        log.info("Publishing command to topic: {}, key: {}", topic, key);
        kafkaTemplate.send(topic, key, command);
    }

}
