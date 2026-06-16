package com.marcos.ecommerce.order_service.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataProductRepository extends JpaRepository<OrderJpaEntity, Long> {

    List<OrderJpaEntity> findByCustomerId(Long customerId);

}
