package com.marcos.ecommerce.payment_service.infrastructure.messaging;

import com.marcos.ecommerce.payment_service.application.messaging.command.ProcessPaymentCommand;
import com.marcos.ecommerce.payment_service.application.port.PaymentEventPublisher;
import com.marcos.ecommerce.payment_service.application.usecase.ProcessPaymentUseCase;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProcessPaymentConsumer {

    private final ProcessPaymentUseCase processPaymentUseCase;
    private final PaymentEventPublisher publisher;

    public ProcessPaymentConsumer(ProcessPaymentUseCase processPaymentUseCase, PaymentEventPublisher publisher) {
        this.processPaymentUseCase = processPaymentUseCase;
        this.publisher = publisher;
    }

    @KafkaListener(topics = "process-payment", groupId = "payment-group")
    public void onProcessPayment(ProcessPaymentCommand command) {
        boolean approved = processPaymentUseCase.execute(command.orderId(), command.amount());
        if (approved) {
            publisher.paymentApproved(command.orderId());
        }
    }

}
