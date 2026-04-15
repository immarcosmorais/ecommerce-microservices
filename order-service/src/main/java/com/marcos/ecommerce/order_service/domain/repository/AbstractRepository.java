package com.marcos.ecommerce.order_service.domain.repository;

import java.util.List;
import java.util.Optional;

public interface AbstractRepository<T>{

    T save(T domain);
    Optional<T> findById(Long id);
    List<T> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);

}
