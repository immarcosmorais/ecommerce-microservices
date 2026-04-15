package com.marcos.ecommerce.order_service.application.usecase;

import com.marcos.ecommerce.order_service.application.dto.CreateOrderRequest;
import com.marcos.ecommerce.order_service.application.dto.OrderItemRequest;
import com.marcos.ecommerce.order_service.application.dto.OrderResponse;
import com.marcos.ecommerce.order_service.application.mapper.OrderMapper;
import com.marcos.ecommerce.order_service.domain.model.Order;
import com.marcos.ecommerce.order_service.domain.repository.OrderRepository;

public class CreateOrderUseCase {

    private final OrderRepository orderRepository;

    public CreateOrderUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderResponse execute(CreateOrderRequest request) {
        Order order = new Order(request.customerId());

        for (OrderItemRequest itemRequest : request.items()) {
            order.addItem(
                    itemRequest.productId(),
                    itemRequest.productName(),
                    itemRequest.quantity(),
                    itemRequest.unitPrice()
            );
        }

        order.confirm();
        Order saved = orderRepository.save(order);
        return OrderMapper.toResponse(saved);
    }

}
