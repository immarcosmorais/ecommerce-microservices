package com.marcos.ecommerce.product_service.application.usecase;

import com.marcos.ecommerce.product_service.domain.exception.ProductNotFoundException;
import com.marcos.ecommerce.product_service.domain.model.Product;
import com.marcos.ecommerce.product_service.domain.repository.ProductRepository;

public class DecreaseStockUseCase {

    private final ProductRepository productRepository;

    public DecreaseStockUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void execute(Long productId, int quantity) {
        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        product.decreaseStock(quantity);
        this.productRepository.save(product);
    }
}
