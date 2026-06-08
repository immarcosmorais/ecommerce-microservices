package com.marcos.ecommerce.orchestrator_service.application.service;

import com.marcos.ecommerce.orchestrator_service.application.messaging.command.ConfirmOrderCommand;
import com.marcos.ecommerce.orchestrator_service.application.messaging.command.ProcessPaymentCommand;
import com.marcos.ecommerce.orchestrator_service.application.messaging.command.ReserveStockCommand;
import com.marcos.ecommerce.orchestrator_service.application.messaging.event.OrderCreatedEvent;
import com.marcos.ecommerce.orchestrator_service.application.messaging.event.PaymentApprovedEvent;
import com.marcos.ecommerce.orchestrator_service.application.messaging.event.StockReservedEvent;
import com.marcos.ecommerce.orchestrator_service.application.port.SagaCommandPublisher;
import com.marcos.ecommerce.orchestrator_service.domain.model.OrderSaga;
import com.marcos.ecommerce.orchestrator_service.domain.repository.OrderSagaRepository;

public class OrderSagaOrchestrator {

    private final OrderSagaRepository sagaRepository;
    private final SagaCommandPublisher publisher;

    public OrderSagaOrchestrator(OrderSagaRepository sagaRepository, SagaCommandPublisher publisher) {
        this.sagaRepository = sagaRepository;
        this.publisher = publisher;
    }

    public void start(OrderCreatedEvent event) {
        OrderSaga saga = new OrderSaga(event.orderId(), event.totalAmount());
        sagaRepository.save(saga);
        var items = event.items().stream()
                .map(i -> new ReserveStockCommand.Item(i.productId(), i.quantity()))
                .toList();
        publisher.reserveStock(new ReserveStockCommand(event.orderId(), items));
    }

    public void onStockReserved(StockReservedEvent event) {
        OrderSaga saga = sagaRepository.findByOrderId(event.orderId()).orElseThrow();
        saga.markStockReserved();
        sagaRepository.save(saga);
        publisher.processPayment(new ProcessPaymentCommand(event.orderId(), saga.getTotalAmount()));
    }

    public void onPaymentApproved(PaymentApprovedEvent event) {
        OrderSaga saga = sagaRepository.findByOrderId(event.orderId()).orElseThrow();
        saga.markPaid();
        saga.complete();
        sagaRepository.save(saga);
        publisher.confirmOrder(new ConfirmOrderCommand(event.orderId()));
    }

}
