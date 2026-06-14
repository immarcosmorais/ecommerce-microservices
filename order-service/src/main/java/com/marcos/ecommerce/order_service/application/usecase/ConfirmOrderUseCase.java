package com.marcos.ecommerce.order_service.application.usercase;

import com.marcos.ecommerce.order_service.domain.exception.OrderNotFoundException;
import com.marcos.ecommerce.order_service.domain.model.Order;
import com.marcos.ecommerce.order_service.domain.repository.OrderRepository;

public class ConfirmOrderUseCase {

    private final OrderRepository orderRepository;

    public ConfirmOrderUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void execute(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()-> new OrderNotFoundException(orderId));
        order.confirm();
        orderRepository.save(order);
    }
}
