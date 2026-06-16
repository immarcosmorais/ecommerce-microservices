package com.marcos.ecommerce.order_service.infrastructure.persistence;

import com.marcos.ecommerce.order_service.domain.model.Order;
import com.marcos.ecommerce.order_service.domain.model.OrderStatus;
import com.marcos.ecommerce.order_service.domain.repository.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
@DisplayName("JpaOrderRepository — testes de integração")
class JpaOrderRepositoryTest {

    @Autowired
    private SpringDataOrderRepository springDataRepository;

    private JpaOrderRepository repository;

    @BeforeEach
    void setUp() {
        repository = new JpaOrderRepository(springDataRepository);
    }

    private Order buildConfirmedOrder(Long customerId) {
        Order order = new Order(customerId);
        order.addItem(1L, "Notebook", 2, new BigDecimal("5000.00"));
        order.confirm();
        return order;
    }

    @Test
    @DisplayName("deve salvar pedido com itens e recuperar pelo ID")
    void shouldSaveOrderWithItemsAndFindById() {
        Order saved = repository.save(buildConfirmedOrder(10L));

        assertThat(saved.getId()).isNotNull();

        Optional<Order> found = repository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getCustomerId()).isEqualTo(10L);
        assertThat(found.get().getStatus()).isEqualTo(OrderStatus.CONFIRMED);
//        assertThat(found.get().getItems()).isEqualTo(1);
//        assertThat(found.get().getItems().get(0).getProductId()).isEqualTo(1L);
        found.ifPresent(order -> assertThat(order.getId()).isEqualTo(saved.getId()));
    }

    @Test
    @DisplayName("deve retornar pedidos paginados")
    void shouldReturnPagedOrders() {
        repository.save(buildConfirmedOrder(1L));
        repository.save(buildConfirmedOrder(2L));
        repository.save(buildConfirmedOrder(3L));

        PageResult<Order> page = repository.findAll(0, 2);

        assertThat(page.content().size()).isEqualTo(2);
        assertThat(page.totalElements()).isEqualTo(3);
        assertThat(page.totalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.isLast()).isFalse();
    }

    @Test
    @DisplayName("deve buscar pedidos por customerId")
    void shouldFindByCustomerId() {
        repository.save(buildConfirmedOrder(10L));
        repository.save(buildConfirmedOrder(10L));
        repository.save(buildConfirmedOrder(99L));

        List<Order> orders = repository.findByCustomerId(10L);

        orders.forEach(order -> {
            assertThat(order.getCustomerId()).isEqualTo(10L);
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        });
    }

    @Test
    @DisplayName("deve retornar lista vazia para customerId sem pedidos")
    void shouldReturnEmptyListForCustomerWithNoOrders() {
        List<Order> orders = repository.findByCustomerId(999L);
        assertThat(orders.isEmpty()).isTrue();
    }

//    @Test
//    @DisplayName("deve persistir mudança de status")
//    void shouldPersistStatusChange() {
//        Order saved = repository.save(buildConfirmedOrder(5L));
//
//        Order loaded = repository.findById(saved.getId()).orElseThrow();
//        loaded.changeStatus(OrderStatus.PROCESSING);
//        repository.save(loaded);
//
//        Order updated = repository.findById(saved.getId()).orElseThrow();
//        assertThat(updated.getStatus()).isEqualTo(OrderStatus.PROCESSING);
//    }

    @Test
    @DisplayName("deve calcular total corretamente após persistência")
    void shouldCalculateTotalAfterPersistence() {
        Order order = new Order(1L);
        order.addItem(1L, "Notebook", 2, new BigDecimal("5000.00")); // 10000
        order.addItem(2L, "Mouse", 1, new BigDecimal("150.00"));     // 150
        order.confirm();
        Order saved = repository.save(order);

        Order found = repository.findById(saved.getId()).orElseThrow();
        assertThat(found.calculateTotal()).isEqualByComparingTo("10150.00");
    }

}