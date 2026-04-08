package com.marcos.ecommerce.product_service.domain.model;

import java.math.BigDecimal;

public class ValidateFields {

    private ValidateFields(){}

    public static void validateString(String field, String fieldName){
        if(field == null|| field.isBlank()){
            throw new IllegalArgumentException(String.format("Field %s can not be empty", fieldName));
        }
        if (field.length() > 255){
            throw new IllegalArgumentException(String.format("Field %s can not be exceed 255 characters", fieldName));
        }
    }

    public static void validateBigDecimal(BigDecimal field, String fieldName){
        if(field == null || field.compareTo(BigDecimal.ZERO) < 1){
            throw new IllegalArgumentException(String.format("Field %s must be greater than 0", fieldName));
        }
    }

    public static void validateInteger(Integer field, String fieldName){
        if (field == null ||  field < 1){
            throw new IllegalArgumentException(String.format("Field %s must be greater than 0", fieldName));
        }
    }

}
