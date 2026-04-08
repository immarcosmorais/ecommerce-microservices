package com.marcos.ecommerce.product_service.application.usercase;

import com.marcos.ecommerce.product_service.application.dto.ProductRequest;
import com.marcos.ecommerce.product_service.application.dto.ProductResponse;
import com.marcos.ecommerce.product_service.application.mapper.ProductMapper;
import com.marcos.ecommerce.product_service.domain.model.Product;
import com.marcos.ecommerce.product_service.domain.repository.ProductRepository;

public class CreateProductUseCase {

    private final ProductRepository productRepository;

    public CreateProductUseCase(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public ProductResponse execute(ProductRequest request){
        Product product = ProductMapper.toDomain(request);
        Product saved = productRepository.save(product);
        return ProductMapper.toResponse(saved);
    }

}
