package com.marcos.ecommerce.order_service.infrastructure.persistence;

import com.marcos.ecommerce.order_service.domain.model.Order;
import com.marcos.ecommerce.order_service.domain.repository.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaOrderRepository implements OrderRepository {

    private final SpringDataProductRepository repository;

    public JpaOrderRepository(SpringDataProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Order> findByCustomerId(Long customerId) {
        return this.repository.findByCustomerId(customerId).stream().map(OrderJpaEntity::toDomain).toList();
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = OrderJpaEntity.fromDomain(order);
        OrderJpaEntity saved = repository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Order> findById(Long id) {
        return repository.findById(id).map(OrderJpaEntity::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return repository.findAll().stream().map(OrderJpaEntity::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
}
