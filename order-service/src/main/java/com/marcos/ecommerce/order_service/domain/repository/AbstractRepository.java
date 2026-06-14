package com.marcos.ecommerce.order_service.domain.repository;

import java.util.Optional;

public interface AbstractRepository<T> {

    T save(T domain);

    Optional<T> findById(Long id);

    PageResult<T> findAll(int page, int size);

    void deleteById(Long id);

    boolean existsById(Long id);

}
