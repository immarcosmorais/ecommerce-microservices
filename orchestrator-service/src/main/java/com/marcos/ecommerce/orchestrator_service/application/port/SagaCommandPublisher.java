package com.marcos.ecommerce.orchestrator_service.application.port;

import com.marcos.ecommerce.orchestrator_service.application.messaging.command.ConfirmOrderCommand;
import com.marcos.ecommerce.orchestrator_service.application.messaging.command.ProcessPaymentCommand;
import com.marcos.ecommerce.orchestrator_service.application.messaging.command.ReserveStockCommand;

public interface SagaCommandPublisher {
    void reserveStock(ReserveStockCommand command);

    void processPayment(ProcessPaymentCommand command);

    void confirmOrder(ConfirmOrderCommand command);
}
