package com.marcos.ecommerce.product_service.application.usercase;

import com.marcos.ecommerce.product_service.application.dto.ProductResponse;
import com.marcos.ecommerce.product_service.application.mapper.ProductMapper;
import com.marcos.ecommerce.product_service.domain.exception.ProductNotFoundException;
import com.marcos.ecommerce.product_service.domain.model.Product;
import com.marcos.ecommerce.product_service.domain.repository.ProductRepository;

import java.util.List;

public class GetProductUseCase {

    private final ProductRepository productRepository;

    public GetProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse finById(long id){
        Product product = this.productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        return ProductMapper.toResponse(product);
    }

    public List<ProductResponse> findAll(){
        return productRepository.findAll().stream().map(ProductMapper::toResponse).toList();
    }

}
