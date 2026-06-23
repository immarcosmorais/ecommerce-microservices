package com.marcos.ecommerce.payment_service.infrastructure.messaging;

import com.marcos.ecommerce.payment_service.application.messaging.event.PaymentApprovedEvent;
import com.marcos.ecommerce.payment_service.application.messaging.event.PaymentFailedEvent;
import com.marcos.ecommerce.payment_service.application.port.PaymentEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPaymentEventPublisher implements PaymentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaPaymentEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaPaymentEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void paymentApproved(PaymentApprovedEvent event) {
        publish("payment-approved", String.valueOf(event.orderId()), event);
    }

    @Override
    public void paymentFailed(PaymentFailedEvent event) {
        publish("payment-failed", String.valueOf(event.orderId()), event);
    }

    private <T> void publish(String topic, String key, T command) {
        log.info("Publishing command to topic: {}, key: {}", topic, key);
        kafkaTemplate.send(topic, key, command);
    }
}
