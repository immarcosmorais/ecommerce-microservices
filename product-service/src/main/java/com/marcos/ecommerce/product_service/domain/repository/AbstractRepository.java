package com.marcos.ecommerce.product_service.domain.repository;

import java.util.List;
import java.util.Optional;

public interface AbstractRepository <T>{

    T save(T entity);
    Optional<T> findById(Long id);
    List<T> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);

}
