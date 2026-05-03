package com.marcos.ecommerce.product_service.infrastructure.persistence;

import com.marcos.ecommerce.product_service.domain.model.PageResult;
import com.marcos.ecommerce.product_service.domain.model.Product;
import com.marcos.ecommerce.product_service.domain.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public PageResult<Product> findAll(int page, int size) {
        Page<ProductJpaEntity> springPage = repository.findAll(PageRequest.of(page, size));
        List<Product> content = springPage.getContent().stream().map(ProductJpaEntity::toDomain).toList();

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
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
}
