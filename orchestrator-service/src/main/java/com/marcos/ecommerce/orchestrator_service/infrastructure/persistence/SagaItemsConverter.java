package com.marcos.ecommerce.orchestrator_service.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcos.ecommerce.orchestrator_service.domain.model.OrderSaga;
import jakarta.persistence.AttributeConverter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SagaItemsConverter implements AttributeConverter<List<OrderSaga.SagaItem>, String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<OrderSaga.SagaItem> items) {
        try {
            return mapper.writeValueAsString(items);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Erro ao serializar SagaItems", e);
        }
    }

    @Override
    public List<OrderSaga.SagaItem> convertToEntityAttribute(String json) {
        try {
            return mapper.readValue(json, new TypeReference<List<OrderSaga.SagaItem>>() {
            });
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Erro ao deserializar SagaItems", e);
        }
    }
}
