package com.marcos.ecommerce.order_service.application.mapper;

import com.marcos.ecommerce.order_service.application.dto.OrderItemResponse;
import com.marcos.ecommerce.order_service.application.dto.OrderResponse;
import com.marcos.ecommerce.order_service.domain.model.Order;
import com.marcos.ecommerce.order_service.domain.model.OrderItem;

import java.util.List;

public class OrderMapper {

    private OrderMapper() {}

    public static OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(OrderMapper::toItemResponse)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus().name(),
                itemResponses,
                order.calculateTotal(),
                order.totalItems(),
                order.getCreatedAt()
        );
    }

    private static OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getProductId(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal()
        );
    }
}
