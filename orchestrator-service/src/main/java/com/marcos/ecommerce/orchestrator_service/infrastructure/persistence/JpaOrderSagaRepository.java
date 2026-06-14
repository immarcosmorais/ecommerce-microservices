package com.marcos.ecommerce.orchestrator_service.infrastructure.persistence;

import com.marcos.ecommerce.orchestrator_service.domain.model.OrderSaga;
import com.marcos.ecommerce.orchestrator_service.domain.repository.OrderSagaRepository;
import com.marcos.ecommerce.orchestrator_service.domain.repository.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaOrderSagaRepository implements OrderSagaRepository {

    private final SpringDataOrderSagaRepository repository;

    public JpaOrderSagaRepository(SpringDataOrderSagaRepository repository) {
        this.repository = repository;
    }

    @Override
    public OrderSaga save(OrderSaga orderSaga) {
        OrderSagaJpaEntity entity = OrderSagaJpaEntity.fromDomain(orderSaga);
        OrderSagaJpaEntity savedEntity = repository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<OrderSaga> findById(Long id) {
        return repository.findById(id).map(OrderSagaJpaEntity::toDomain);
    }

    @Override
    public PageResult<OrderSaga> findAll(int page, int size) {
        Page<OrderSagaJpaEntity> springPage = repository.findAll(PageRequest.of(page, size));
        List<OrderSaga> content = springPage.getContent().stream().map(OrderSagaJpaEntity::toDomain).toList();
        return new PageResult<>(
                content,
                springPage.getNumber(),
                springPage.getSize(),
                springPage.getTotalElements(),
                springPage.getTotalPages()
        );
    }

    @Override
    public void deleteById(Long id) {
        this.repository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return this.repository.existsById(id);
    }

    @Override
    public Optional<OrderSaga> findByOrderId(Long orderId) {
        return repository.findByOrderId(orderId).map(OrderSagaJpaEntity::toDomain);
    }
}
