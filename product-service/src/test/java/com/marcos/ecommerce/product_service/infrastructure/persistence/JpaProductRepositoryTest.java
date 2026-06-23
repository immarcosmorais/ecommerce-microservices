package com.marcos.ecommerce.product_service.infrastructure.persistence;

import com.marcos.ecommerce.product_service.domain.model.Product;
import com.marcos.ecommerce.product_service.domain.repository.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

// Testes de integração contra banco H2 em memória — carregam apenas a camada JPA
@DataJpaTest
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
@DisplayName("JpaProductRepository — testes de integração com banco em memória")
class JpaProductRepositoryTest {

    @Autowired
    private SpringDataProductRepository springDataRepository;

    private JpaProductRepository repository;

    @BeforeEach
    void setUp() {
        repository = new JpaProductRepository(springDataRepository);
    }

    private Product saveProduct(String name, int stock) {
        Product product = new Product(name, "Descrição de " + name, new BigDecimal("999.99"), stock);
        return repository.save(product);
    }

    @Test
    @DisplayName("deve salvar e recuperar produto pelo ID")
    void shouldSaveAndFindById() {
        // Arrange + Act
        Product saved = saveProduct("Notebook Dell", 10);

        // Assert
        assertThat(saved.getId()).isNotNull();
        Optional<Product> found = repository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Notebook Dell");
        assertThat(found.get().getStockQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("deve retornar Optional vazio para ID inexistente")
    void shouldReturnEmptyOptionalForNonExistentId() {
        Optional<Product> found = repository.findById(9999L);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("deve retornar primeira página com tamanho correto e metadados de paginação")
    void shouldReturnPagedProductsWithCorrectMetadata() {
        // Arrange
        saveProduct("Produto A", 5);
        saveProduct("Produto B", 3);
        saveProduct("Produto C", 8);

        // Act
        PageResult<Product> page = repository.findAll(0, 2);

        // Assert — T2: corrigido page.content() → page.content().size()
        assertThat(page.content().size()).isEqualTo(2);
        assertThat(page.totalElements()).isEqualTo(3);
        assertThat(page.totalPages()).isEqualTo(2);
        assertThat(page.page()).isZero();
        assertThat(page.isFirst()).isTrue();
        assertThat(page.isLast()).isFalse();
    }

    @Test
    @DisplayName("deve retornar segunda página corretamente")
    void shouldReturnSecondPageCorrectly() {
        // Arrange
        saveProduct("Produto A", 5);
        saveProduct("Produto B", 3);
        saveProduct("Produto C", 8);

        // Act
        PageResult<Product> page = repository.findAll(1, 2);

        // Assert — T2: corrigido page.content() → page.content().size()
        assertThat(page.content().size()).isEqualTo(1);
        assertThat(page.page()).isEqualTo(1);
        assertThat(page.isLast()).isTrue();
    }

    @Test
    @DisplayName("deve decrementar estoque e persistir corretamente")
    void shouldDecreaseStockAndPersist() {
        // Arrange
        Product saved = saveProduct("Produto Estoque", 20);
        Product loaded = repository.findById(saved.getId()).orElseThrow();

        // Act
        loaded.decreaseStock(7);
        repository.save(loaded);

        // Assert
        Product updated = repository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getStockQuantity()).isEqualTo(13);
    }

    @Test
    @DisplayName("deve deletar produto pelo ID e não encontrá-lo depois")
    void shouldDeleteProductByIdAndNotFindItAfterwards() {
        // Arrange
        Product saved = saveProduct("Para Deletar", 1);
        Long id = saved.getId();

        // Act
        repository.deleteById(id);

        // Assert
        assertThat(repository.findById(id)).isEmpty();
        assertThat(repository.existsById(id)).isFalse();
    }

    @Test
    @DisplayName("existsById deve retornar true para produto existente e false após deleção")
    void shouldReturnTrueForExistingProductAndFalseAfterDeletion() {
        // Arrange
        Product saved = saveProduct("Existente", 1);

        // Assert — antes da deleção
        assertThat(repository.existsById(saved.getId())).isTrue();

        // Act + Assert — após deleção
        repository.deleteById(saved.getId());
        assertThat(repository.existsById(saved.getId())).isFalse();
    }
}
