package com.marcos.ecommerce.order_service.application.usecase;

import com.marcos.ecommerce.order_service.application.dto.OrderResponse;
import com.marcos.ecommerce.order_service.application.mapper.OrderMapper;
import com.marcos.ecommerce.order_service.domain.exception.OrderNotFoundException;
import com.marcos.ecommerce.order_service.domain.model.Order;
import com.marcos.ecommerce.order_service.domain.repository.OrderRepository;
import com.marcos.ecommerce.order_service.application.dto.PagedResponse;

import com.marcos.ecommerce.order_service.domain.model.PageResult;

import java.util.List;

public class GetOrderUseCase {

    private final OrderRepository orderRepository;

    public GetOrderUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return OrderMapper.toResponse(order);
    }

    public PagedResponse<OrderResponse> findAll(int page, int size) {
        PageResult<Order> pageResult = orderRepository.findAll(page, size);
        PageResult<OrderResponse> mapped = new PageResult<>(
                pageResult.content().stream().map(OrderMapper::toResponse).toList(),
                pageResult.page(),
                pageResult.size(),
                pageResult.totalElements(),
                pageResult.totalPages()
        );
        return PagedResponse.from(mapped);
    }

    public List<OrderResponse> findByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

}
