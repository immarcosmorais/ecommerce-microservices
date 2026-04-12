package com.marcos.ecommerce.product_service.infrastructure.config;

import com.marcos.ecommerce.product_service.application.usercase.CreateProductUseCase;
import com.marcos.ecommerce.product_service.application.usercase.DeleteProductUseCase;
import com.marcos.ecommerce.product_service.application.usercase.GetProductUseCase;
import com.marcos.ecommerce.product_service.application.usercase.UpdateProductUseCase;
import com.marcos.ecommerce.product_service.domain.repository.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateProductUseCase createProductUseCase(ProductRepository productRepository) {
        return new CreateProductUseCase(productRepository);
    }

    @Bean
    public GetProductUseCase getProductUseCase(ProductRepository productRepository) {
        return new GetProductUseCase(productRepository);
    }

    @Bean
    public UpdateProductUseCase updateProductUseCase(ProductRepository productRepository) {
        return new UpdateProductUseCase(productRepository);
    }

    @Bean
    public DeleteProductUseCase deleteProductUseCase(ProductRepository productRepository) {
        return new DeleteProductUseCase(productRepository);
    }

}
