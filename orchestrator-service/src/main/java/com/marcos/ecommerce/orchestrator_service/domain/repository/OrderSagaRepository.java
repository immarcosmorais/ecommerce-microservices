package com.marcos.ecommerce.orchestrator_service.domain.repository;

import com.marcos.ecommerce.orchestrator_service.domain.model.OrderSaga;

import java.util.Optional;

public interface OrderSagaRepository {
    OrderSaga save(OrderSaga orderSaga);
    Optional<OrderSaga> findById(Long id);
    Optional<OrderSaga> findByOrderId(Long orderId);
}
