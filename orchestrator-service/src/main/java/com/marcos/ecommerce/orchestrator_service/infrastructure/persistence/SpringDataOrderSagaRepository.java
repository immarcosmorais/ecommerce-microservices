package com.marcos.ecommerce.orchestrator_service.infrastructure.persistence;

import com.marcos.ecommerce.orchestrator_service.domain.model.OrderSaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataOrderSagaRepository extends JpaRepository<OrderSagaJpaEntity, Long> {
    Optional<OrderSagaJpaEntity> findByOrderId(Long orderId);
}
