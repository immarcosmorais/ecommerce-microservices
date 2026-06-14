package com.marcos.ecommerce.orchestrator_service.domain.repository;

import com.marcos.ecommerce.orchestrator_service.domain.model.OrderSaga;

import java.util.Optional;

public interface OrderSagaRepository extends AbstractRepository<OrderSaga> {
    //    OrderSaga save(OrderSaga orderSaga);
//    Optional<OrderSaga> findById(Long id);
    Optional<OrderSaga> findByOrderId(Long orderId);
}
