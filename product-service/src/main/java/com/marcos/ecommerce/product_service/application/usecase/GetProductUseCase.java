package com.marcos.ecommerce.product_service.application.usecase;

import com.marcos.ecommerce.product_service.application.dto.PagedResponse;
import com.marcos.ecommerce.product_service.application.dto.ProductResponse;
import com.marcos.ecommerce.product_service.application.mapper.ProductMapper;
import com.marcos.ecommerce.product_service.domain.exception.ProductNotFoundException;
import com.marcos.ecommerce.product_service.domain.model.PageResult;
import com.marcos.ecommerce.product_service.domain.model.Product;
import com.marcos.ecommerce.product_service.domain.repository.ProductRepository;
import org.springframework.data.domain.Page;

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

    public PagedResponse<ProductResponse> findAll(int page, int size){
        PageResult<Product> pageResult = this.productRepository.findAll(page, size);
        PageResult<ProductResponse> mapped = new PageResult<>(
                pageResult.content().stream().map(ProductMapper::toResponse).toList(),
                pageResult.page(),
                pageResult.size(),
                pageResult.totalElements(),
                pageResult.totalPages()
        );
        return PagedResponse.from(mapped);
    }

}
