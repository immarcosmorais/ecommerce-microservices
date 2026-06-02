package com.marcos.ecommerce.order_service.application.usecase;


import com.marcos.ecommerce.order_service.application.dto.CreateOrderRequest;
import com.marcos.ecommerce.order_service.application.dto.OrderItemRequest;
import com.marcos.ecommerce.order_service.application.dto.OrderResponse;
import com.marcos.ecommerce.order_service.domain.event.OrderCreatedEvent;
import com.marcos.ecommerce.order_service.domain.event.OrderEventPublisher;
import com.marcos.ecommerce.order_service.domain.model.Order;
import com.marcos.ecommerce.order_service.domain.model.OrderStatus;
import com.marcos.ecommerce.order_service.domain.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateOrderUseCase")
class CreateOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventPublisher eventPublisher;

    private CreateOrderUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateOrderUseCase(orderRepository, eventPublisher);
    }

    private CreateOrderRequest buildRequest() {
        List<OrderItemRequest> items = List.of(
                new OrderItemRequest(1L, "Notebook", 2, new BigDecimal("5000.00")),
                new OrderItemRequest(2L, "Mouse", 1, new BigDecimal("150.00"))
        );
        return new CreateOrderRequest(10L, items);
    }

    private Order buildSavedOrder() {
        Order order = new Order(10L);
        order.addItem(1L, "Notebook", 2, new BigDecimal("5000.00"));
        order.addItem(2L, "Mouse", 1, new BigDecimal("150.00"));
        order.confirm();
        order.setId(1L);
        return order;
    }

    @Test
    @DisplayName("deve criar pedido, salvar e publicar evento Kafka")
    void shouldCreateOrderSaveAndPublishEvent() {
        Order savedOrder = buildSavedOrder();
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        OrderResponse response = useCase.execute(buildRequest());
        assertThat(response.customerId()).isEqualTo(10L);
        assertThat(response.status()).isEqualTo("CONFIRMED");
        assertThat(response.items().size()).isEqualTo(2);
        assertThat(response.total()).isEqualByComparingTo("10150.00");
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(eventPublisher, times(1)).publisherOrderCreated(any(OrderCreatedEvent.class));
    }

    @Test
    @DisplayName("deve publicar evento com dados corretos do pedido")
    void shouldPublishEventWithCorrectData() {
        Order savedOrder = buildSavedOrder();
        when(orderRepository.save(any())).thenReturn(savedOrder);
        ArgumentCaptor<OrderCreatedEvent> eventCaptor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
        useCase.execute(buildRequest());
        verify(eventPublisher).publisherOrderCreated(eventCaptor.capture());
        OrderCreatedEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.orderId()).isEqualTo(1L);
        assertThat(capturedEvent.customerId()).isEqualTo(10L);
        assertThat(capturedEvent.items().size()).isEqualTo(2);
        assertThat(capturedEvent.totalAmount()).isEqualByComparingTo("10150.00");
    }

    @Test
    @DisplayName("deve salvar pedido com status CONFIRMED")
    void shouldSaveOrderWithConfirmedStatus() {
        Order savedOrder = buildSavedOrder();
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        useCase.execute(buildRequest());
        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

}