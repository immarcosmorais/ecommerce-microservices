package com.marcos.ecommerce.product_service.domain.repository;

import com.marcos.ecommerce.product_service.domain.model.PageResult;

import java.util.List;
import java.util.Optional;

public interface AbstractRepository <T>{

    T save(T domain);
    Optional<T> findById(Long id);
    PageResult<T> findAll(int page, int size);
    void deleteById(Long id);
    boolean existsById(Long id);

}
