package com.marcos.ecommerce.order_service.application.usecase;

import com.marcos.ecommerce.order_service.application.dto.CreateOrderRequest;
import com.marcos.ecommerce.order_service.application.dto.OrderItemRequest;
import com.marcos.ecommerce.order_service.application.dto.OrderResponse;
import com.marcos.ecommerce.order_service.application.mapper.OrderMapper;
import com.marcos.ecommerce.order_service.domain.event.OrderCreatedEvent;
import com.marcos.ecommerce.order_service.domain.event.OrderEventPublisher;
import com.marcos.ecommerce.order_service.domain.model.Order;
import com.marcos.ecommerce.order_service.domain.repository.OrderRepository;

import java.util.List;

public class CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    public CreateOrderUseCase(OrderRepository orderRepository, OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
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

        List<OrderCreatedEvent.OrderItemEventDto> itemDtos = saved.getItems().stream().map(item -> new OrderCreatedEvent.OrderItemEventDto(
                item.getProductId(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice()
        )).toList();

        eventPublisher.publisherOrderCreated(
                new OrderCreatedEvent(
                        saved.getId(),
                        saved.getCustomerId(),
                        itemDtos,
                        saved.calculateTotal()
                )
        );

        return OrderMapper.toResponse(saved);
    }

}
