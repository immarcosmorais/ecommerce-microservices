package com.marcos.ecommerce.order_service.infrastructure.persistence;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SpringDataOrderRepository extends JpaRepository<OrderJpaEntity, Long> {

    List<OrderJpaEntity> findByCustomerId(Long customerId);

    @Query("SELECT o FROM OrderJpaEntity o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<OrderJpaEntity> findByIdWithItems(@Param("id") Long id);

}
