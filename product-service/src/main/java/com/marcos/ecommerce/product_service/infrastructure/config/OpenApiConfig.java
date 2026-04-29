package com.marcos.ecommerce.product_service.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product Service API")
                        .description("Gerenciamento de produtos e estoque do e-commerce")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Marcos Morais")
                                .email("marcos.morais@contact@gmail.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8081").description("Local"),
                        new Server().url("http://localhost:8080/product-service").description("Via Gateway")
                ));
    }

}
