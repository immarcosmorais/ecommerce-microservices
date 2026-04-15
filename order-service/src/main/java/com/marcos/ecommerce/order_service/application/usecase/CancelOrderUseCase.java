package com.marcos.ecommerce.order_service.application.usecase;

import com.marcos.ecommerce.order_service.application.dto.OrderResponse;
import com.marcos.ecommerce.order_service.application.mapper.OrderMapper;
import com.marcos.ecommerce.order_service.domain.exception.OrderNotFoundException;
import com.marcos.ecommerce.order_service.domain.model.Order;
import com.marcos.ecommerce.order_service.domain.repository.OrderRepository;

public class CancelOrderUseCase {

    private final OrderRepository orderRepository;

    public CancelOrderUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderResponse execute(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        order.cancel();
        Order saved = orderRepository.save(order);
        return OrderMapper.toResponse(saved);
    }

}
