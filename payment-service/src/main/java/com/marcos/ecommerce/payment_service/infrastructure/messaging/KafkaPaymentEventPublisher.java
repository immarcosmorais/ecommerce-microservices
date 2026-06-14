package com.marcos.ecommerce.payment_service.infrastructure.messaging;

import com.marcos.ecommerce.payment_service.application.messaging.event.PaymentApprovedEvent;
import com.marcos.ecommerce.payment_service.application.port.PaymentEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPaymentEventPublisher implements PaymentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaPaymentEventPublisher.class);
    private static final String TOPIC = "payment-approved";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaPaymentEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void paymentApproved(PaymentApprovedEvent event) {
        log.info("Publishing event to topic: {}, key: {}", TOPIC, event.orderId());
        this.kafkaTemplate.send(TOPIC, String.valueOf(event.orderId()), event);
    }
}
