package com.marcos.ecommerce.product_service.application.usercase;

import com.marcos.ecommerce.product_service.application.dto.ProductRequest;
import com.marcos.ecommerce.product_service.application.dto.ProductResponse;
import com.marcos.ecommerce.product_service.application.mapper.ProductMapper;
import com.marcos.ecommerce.product_service.domain.exception.ProductNotFoundException;
import com.marcos.ecommerce.product_service.domain.model.Product;
import com.marcos.ecommerce.product_service.domain.repository.ProductRepository;

public class UpdateProductUseCase {

    private final ProductRepository productRepository;

    public UpdateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse execute(Long id, ProductRequest request){
        Product product = this.productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        product.updateDetails(
                request.name(),
                request.description(),
                request.price()
        );
        Product updated = this.productRepository.save(product);
        return ProductMapper.toResponse(updated);
    }
}
