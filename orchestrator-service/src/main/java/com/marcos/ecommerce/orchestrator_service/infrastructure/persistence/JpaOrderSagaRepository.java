package com.marcos.ecommerce.orchestrator_service.infrastructure.persistence;

import com.marcos.ecommerce.orchestrator_service.domain.model.OrderSaga;
import com.marcos.ecommerce.orchestrator_service.domain.repository.OrderSagaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaOrderSagaRepository implements OrderSagaRepository {

    private final SpringDataOrderSagaRepository repository;

    public JpaOrderSagaRepository(SpringDataOrderSagaRepository repository) {
        this.repository = repository;
    }

    @Override
    public OrderSaga save(OrderSaga orderSaga) {
        OrderSagaJpaEntity entity =  OrderSagaJpaEntity.fromDomain(orderSaga);
        return repository.save(entity).toDomain();
    }

    @Override
    public Optional<OrderSaga> findById(Long id) {
        return repository.findById(id).map(OrderSagaJpaEntity::toDomain);
    }

    @Override
    public Optional<OrderSaga> findByOrderId(Long orderId) {
        return repository.findByOrderId(orderId).map(OrderSagaJpaEntity::toDomain);
    }
}
