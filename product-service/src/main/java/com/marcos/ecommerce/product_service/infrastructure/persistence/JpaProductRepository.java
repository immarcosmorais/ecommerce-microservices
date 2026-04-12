package com.marcos.ecommerce.product_service.infrastructure.persistence;

import com.marcos.ecommerce.product_service.domain.model.Product;
import com.marcos.ecommerce.product_service.domain.repository.ProductRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaProductRepository implements ProductRepository {

    private final SpringDataProductRepository repository;

    public JpaProductRepository(SpringDataProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Product save(Product domain) {
        ProductJpaEntity entity = ProductJpaEntity.fromDomain(domain);
        ProductJpaEntity saved = repository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return repository.findById(id).map(ProductJpaEntity::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return repository.findAll().stream().map(ProductJpaEntity::toDomain).toList();
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
