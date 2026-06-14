package com.marcos.ecommerce.product_service.domain.model;

import java.util.List;

public record PageResult <T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
){
    public boolean isFirst(){
        return page == 0;
    }

    public boolean isLast(){
        return page >= totalPages - 1;
    }

    public boolean hasNext(){
        return page < totalPages - 1;
    }

}
