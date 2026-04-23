package com.marcos.ecommerce.product_service.application.usecase;

import com.marcos.ecommerce.product_service.domain.exception.ProductNotFoundException;
import com.marcos.ecommerce.product_service.domain.repository.ProductRepository;

public class DeleteProductUseCase {

    private final ProductRepository productRepository;

    public DeleteProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void execute(Long id){
        if(!this.productRepository.existsById(id)){
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

}
