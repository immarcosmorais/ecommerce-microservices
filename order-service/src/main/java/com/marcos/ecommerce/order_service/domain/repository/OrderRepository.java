package com.marcos.ecommerce.order_service.domain.repository;

import com.marcos.ecommerce.order_service.domain.model.Order;

import java.util.List;

public interface OrderRepository extends AbstractRepository<Order>{
    List<Order> findByCustomerId(Long customerId);
}
