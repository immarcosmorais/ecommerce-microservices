package com.marcos.ecommerce.orchestrator_service.infrastructure.messaging;

import com.marcos.ecommerce.orchestrator_service.application.messaging.command.ConfirmOrderCommand;
import com.marcos.ecommerce.orchestrator_service.application.messaging.command.ProcessPaymentCommand;
import com.marcos.ecommerce.orchestrator_service.application.messaging.command.ReserveStockCommand;
import com.marcos.ecommerce.orchestrator_service.application.port.SagaCommandPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaSagaCommandPublisher implements SagaCommandPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaSagaCommandPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void reserveStock(ReserveStockCommand c) {
        kafkaTemplate.send("reserve-stock", String.valueOf(c.orderId()), c);
    }

    @Override
    public void processPayment(ProcessPaymentCommand c) {
        kafkaTemplate.send("process-payment", String.valueOf(c.orderId()), c);
    }

    @Override
    public void confirmOrder(ConfirmOrderCommand c) {
        kafkaTemplate.send("confirm-order", String.valueOf(c.orderId()), c);
    }
}
