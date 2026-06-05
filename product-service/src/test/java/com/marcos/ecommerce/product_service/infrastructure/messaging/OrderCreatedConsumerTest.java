package com.marcos.ecommerce.product_service.infrastructure.messaging;

import com.marcos.ecommerce.product_service.domain.event.OrderCreatedEvent;
import com.marcos.ecommerce.product_service.domain.model.Product;
import com.marcos.ecommerce.product_service.domain.repository.ProductRepository;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.datasource.url=jdbc:h2:mem:product_it;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
@EmbeddedKafka(partitions = 1, topics = {"order-created"})
@DisplayName("Fluxo Kafka order→product — integração com broker embarcado")
class OrderCreatedConsumerTest {

    private static final String TOPIC = "order-created";

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private KafkaListenerEndpointRegistry endpointRegistry;

    private KafkaTemplate<String, OrderCreatedEvent> testKafkaTemplate;

    @BeforeEach
    void setUp() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafka.getBrokersAsString());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        config.put(JacksonJsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        ProducerFactory<String, OrderCreatedEvent> pf = new DefaultKafkaProducerFactory<>(config);
        testKafkaTemplate = new KafkaTemplate<>(pf);

        endpointRegistry.getListenerContainers()
                .forEach(c -> ContainerTestUtils.waitForAssignment(c, 1));
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    @DisplayName("deve baixar o estoque ao consumir OrderCreatedEvent")
    void shouldDecreaseStockOnOrderCreatedEvent() {
        // Arrange — produto com estoque conhecido
        Product saved = productRepository.save(
                new Product("Notebook Dell", "i7 16GB", new BigDecimal("4999.90"), 10));
        Long productId = saved.getId();

        OrderCreatedEvent event = new OrderCreatedEvent(
                1L, 99L,
                List.of(new OrderCreatedEvent.OrderItemEventDto(
                        productId, "Notebook Dell", 3, new BigDecimal("4999.90"))),
                new BigDecimal("14999.70"));

        // Act — publica como o order-service publicaria
        testKafkaTemplate.send(TOPIC, String.valueOf(event.orderId()), event);

        // Assert — consumo é assíncrono, então esperamos a baixa de 10 → 7
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            Product updated = productRepository.findById(productId).orElseThrow();
            assertThat(updated.getStockQuantity()).isEqualTo(7);
        });
    }
}