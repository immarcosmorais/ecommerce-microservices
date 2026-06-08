package com.marcos.ecommerce.payment_service.infrastructure.messaging;

import com.marcos.ecommerce.payment_service.application.messaging.event.PaymentApprovedEvent;
import com.marcos.ecommerce.payment_service.application.port.PaymentEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPaymentEventPublisher implements PaymentEventPublisher {

    private static final String TOPIC = "payment-approved";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaPaymentEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void paymentApproved(Long orderId) {
        this.kafkaTemplate.send(TOPIC, String.valueOf(orderId), new PaymentApprovedEvent(orderId));
    }
}
