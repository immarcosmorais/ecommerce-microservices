package com.marcos.ecommerce.orchestrator_service.application.service;

import com.marcos.ecommerce.orchestrator_service.application.messaging.command.*;
import com.marcos.ecommerce.orchestrator_service.application.messaging.event.OrderCreatedEvent;
import com.marcos.ecommerce.orchestrator_service.application.messaging.event.PaymentApprovedEvent;
import com.marcos.ecommerce.orchestrator_service.application.messaging.event.PaymentFailedEvent;
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
        var items = event.items().stream()
                .map(i -> new OrderSaga.SagaItem(i.productId(), i.quantity()))
                .toList();
        OrderSaga saga = new OrderSaga(event.orderId(), event.totalAmount(), items);
        sagaRepository.save(saga);
        var stockItems = items.stream()
                .map(i -> new ReserveStockCommand.Item(i.productId(), i.quantity()))
                .toList();
        publisher.reserveStock(new ReserveStockCommand(event.orderId(), stockItems));
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

    public void onPaymentFailed(PaymentFailedEvent event) {
        OrderSaga saga = sagaRepository.findByOrderId(event.orderId()).orElseThrow();
        saga.startCompensation();
        sagaRepository.save(saga);
        var restoreItems = saga.getItems().stream()
                .map(i -> new RestoreStockCommand.Item(i.productId(), i.quantity()))
                .toList();
        publisher.restoreStock(new RestoreStockCommand(saga.getOrderId(), restoreItems));
        publisher.cancelOrder(new CancelOrderCommand(saga.getOrderId()));
    }

}
