package com.marcos.ecommerce.product_service.infrastructure.config;

import com.marcos.ecommerce.product_service.application.usecase.*;
import com.marcos.ecommerce.product_service.domain.repository.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateProductUseCase createProductUseCase(ProductRepository repository) {
        return new CreateProductUseCase(repository);
    }

    @Bean
    public GetProductUseCase getProductUseCase(ProductRepository repository) {
        return new GetProductUseCase(repository);
    }

    @Bean
    public UpdateProductUseCase updateProductUseCase(ProductRepository repository) {
        return new UpdateProductUseCase(repository);
    }

    @Bean
    public DeleteProductUseCase deleteProductUseCase(ProductRepository repository) {
        return new DeleteProductUseCase(repository);
    }

    @Bean
    public DecreaseStockUseCase decreaseStockUseCase(ProductRepository repository) {
        return new DecreaseStockUseCase(repository);
    }

}
