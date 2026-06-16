package com.marcos.ecommerce.orchestrator_service.application.port;

import com.marcos.ecommerce.orchestrator_service.application.messaging.command.*;

public interface SagaCommandPublisher {
    void reserveStock(ReserveStockCommand command);

    void processPayment(ProcessPaymentCommand command);

    void confirmOrder(ConfirmOrderCommand command);

    void restoreStock(RestoreStockCommand command);

    void cancelOrder(CancelOrderCommand command);

}
