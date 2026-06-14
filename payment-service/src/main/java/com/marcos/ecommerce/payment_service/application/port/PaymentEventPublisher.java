package com.marcos.ecommerce.payment_service.application.port;

import com.marcos.ecommerce.payment_service.application.messaging.event.PaymentApprovedEvent;

public interface PaymentEventPublisher {
    void paymentApproved(PaymentApprovedEvent event);
}
