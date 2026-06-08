package com.marcos.ecommerce.payment_service.application.port;

public interface PaymentEventPublisher {
    void paymentApproved(Long orderId);
}
