package com.marcos.ecommerce.product_service.application.mapper;

import com.marcos.ecommerce.product_service.application.dto.ProductRequest;
import com.marcos.ecommerce.product_service.application.dto.ProductResponse;
import com.marcos.ecommerce.product_service.domain.model.Product;

public class ProductMapper {

    private ProductMapper(){}

    public static Product toDomain(ProductRequest request){
        return new Product(
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity()
        );
    }

    public static ProductResponse toResponse(Product product){
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.isAvailable(),
                product.getCreatedAt()
        );
    }

}
