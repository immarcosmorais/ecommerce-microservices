package com.marcos.ecommerce.product_service.application.usecase;

import com.marcos.ecommerce.product_service.domain.exception.ProductNotFoundException;
import com.marcos.ecommerce.product_service.domain.model.Product;
import com.marcos.ecommerce.product_service.domain.repository.ProductRepository;

public class DecreaseStockUseCase {

    private final ProductRepository repository;

    public DecreaseStockUseCase(ProductRepository repository) {
        this.repository = repository;
    }

    public void execute(Long productId, int quantity) {
        Product product = this.repository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
        product.decreaseStock(quantity);
        this.repository.save(product);
    }

}
