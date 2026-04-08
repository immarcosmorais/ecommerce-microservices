package com.marcos.ecommerce.product_service.domain.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id){
        super(String.format("Product not found with id %d: ", id));
    }
}
