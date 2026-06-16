# ecommerce-microservices

A production-grade microservices architecture implementing the **Saga Orchestration Pattern** for distributed transactions, built with Java 21 and Spring Boot 4.

## Architecture Overview

```
                          ┌─────────────────┐
                          │     Gateway      │  :8080
                          │  (Spring Cloud)  │
                          └────────┬─────────┘
                                   │
              ┌────────────────────┼────────────────────┐
              │                    │                     │
     ┌────────▼────────┐  ┌───────▼────────┐           │
     │  product-service│  │  order-service │           │
     │     :8081       │  │     :8082      │           │
     │   product_db    │  │   order_db     │           │
     └────────┬────────┘  └───────┬────────┘           │
              │                   │                     │
              └─────────┬─────────┘                     │
                        │           Apache Kafka         │
              ┌─────────▼──────────────────────────┐    │
              │         orchestrator-service        │    │
              │              :8085                 │    │
              │            saga_db                 │    │
              └─────────────────────────────────────┘    │
                        │                               │
              ┌─────────▼─────────┐                     │
              │  payment-service  │                     │
              │      :8083        │                     │
              └───────────────────┘                     │
                                          ┌─────────────▼────┐
                                          │notification-service│
                                          │      :8084         │
                                          └────────────────────┘
```

## Saga Orchestration Flow

### Happy Path
```
POST /api/orders
  → order-service    publishes  [order-created]
  → orchestrator     consumes   [order-created]   → STARTED
                     publishes  [reserve-stock]
  → product-service  consumes   [reserve-stock]   → decreases stock
                     publishes  [stock-reserved]
  → orchestrator     consumes   [stock-reserved]  → STOCK_RESERVED
                     publishes  [process-payment]
  → payment-service  consumes   [process-payment] → approves
                     publishes  [payment-approved]
  → orchestrator     consumes   [payment-approved]→ PAID
                     publishes  [confirm-order]
  → order-service    consumes   [confirm-order]   → CONFIRMED
  → orchestrator                                  → COMPLETED ✓
```

### Compensation Path (payment failure)
```
  → payment-service  publishes  [payment-failed]
  → orchestrator     consumes   [payment-failed]  → COMPENSATING
                     publishes  [restore-stock]
                     publishes  [cancel-order]
  → product-service  consumes   [restore-stock]   → restores stock
  → order-service    consumes   [cancel-order]    → CANCELLED
  → orchestrator                                  → CANCELLED ✓
```

## Services

| Service | Port | Database | Responsibility |
|---|---|---|---|
| config-server | 8888 | — | Centralized configuration (Spring Cloud Config) |
| discovery-server | 8761 | — | Service registry (Netflix Eureka) |
| gateway | 8080 | — | API Gateway + load balancing (Spring Cloud Gateway) |
| product-service | 8081 | product_db :5432 | Product catalog + stock management |
| order-service | 8082 | order_db :5433 | Order lifecycle management |
| payment-service | 8083 | — | Payment processing |
| orchestrator-service | 8085 | saga_db :5434 | Saga state machine + compensation coordinator |
| notification-service | 8084 | — | Event-driven notifications (skeleton) |

## Kafka Topics

| Topic | Producer | Consumer | Purpose |
|---|---|---|---|
| `order-created` | order-service | orchestrator | Starts the saga |
| `reserve-stock` | orchestrator | product-service | Commands stock reservation |
| `stock-reserved` | product-service | orchestrator | Confirms stock reserved |
| `process-payment` | orchestrator | payment-service | Commands payment processing |
| `payment-approved` | payment-service | orchestrator | Confirms payment approved |
| `payment-failed` | payment-service | orchestrator | Triggers compensation |
| `confirm-order` | orchestrator | order-service | Commands order confirmation |
| `restore-stock` | orchestrator | product-service | Compensation: restores stock |
| `cancel-order` | orchestrator | order-service | Compensation: cancels order |

## Tech Stack

- **Java 21** + **Spring Boot 4.0.5**
- **Spring Cloud 2025.1.1** — Config Server, Eureka, Gateway
- **Apache Kafka** (Confluent 7.4.0) — async messaging
- **PostgreSQL 16** — per-service databases (database-per-service pattern)
- **Spring Data JPA** + **Hibernate** — persistence
- **Docker Compose** — local infrastructure
- **Clean Architecture** + **DDD** — package structure per service

## Prerequisites

- Java 21+
- Docker + Docker Compose
- Maven 3.9+

## Running Locally

### 1. Start infrastructure

```bash
docker compose up -d
```

Starts: PostgreSQL (×3), Kafka, Zookeeper, Kafka UI.

### 2. Start services (in order)

```bash
# Each in a separate terminal
mvn spring-boot:run -pl config-server
mvn spring-boot:run -pl discovery-server
mvn spring-boot:run -pl gateway
mvn spring-boot:run -pl product-service
mvn spring-boot:run -pl order-service
mvn spring-boot:run -pl payment-service
mvn spring-boot:run -pl orchestrator-service
```

Wait for each service to register in Eureka before starting the next.

### 3. Verify startup

- **Eureka dashboard:** http://localhost:8761
- **Kafka UI:** http://localhost:8090

All services should appear as `UP` in Eureka.

## Testing the Saga

### Create a product

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Notebook Dell XPS",
    "description": "15-inch laptop",
    "price": 8999.99,
    "stockQuantity": 10
  }'
```

### Happy path — order below R$500 (payment approved)

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "items": [
      {
        "productId": 1,
        "productName": "Notebook Dell XPS",
        "quantity": 1,
        "unitPrice": 299.99
      }
    ]
  }'
```

Expected: order status → `CONFIRMED`, stock decremented, saga → `COMPLETED`.

### Compensation path — order above R$500 (payment rejected)

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "items": [
      {
        "productId": 1,
        "productName": "Notebook Dell XPS",
        "quantity": 1,
        "unitPrice": 999.99
      }
    ]
  }'
```

Expected: order status → `CANCELLED`, stock restored, saga → `CANCELLED`.

### Check results

```bash
# Order status
curl http://localhost:8080/api/orders/{id}

# Stock level
curl http://localhost:8080/api/products/{id}
```

## Project Structure

Each service follows Clean Architecture with strict dependency rules:

```
{service}/src/main/java/com/marcos/ecommerce/{service_name}/
├── domain/
│   ├── model/          # Aggregates, entities, value objects
│   ├── repository/     # Repository ports (interfaces)
│   └── exception/      # Domain exceptions
├── application/
│   ├── usecase/        # Business logic (no Spring annotations)
│   ├── messaging/      # Command and event records
│   └── port/           # Publisher ports (interfaces)
└── infrastructure/
    ├── messaging/       # Kafka consumers, producers, publishers
    ├── persistence/     # JPA entities, Spring Data repositories
    ├── rest/            # Controllers, DTOs, exception handlers
    └── config/          # Spring beans wiring (UseCaseConfig)
```

**Dependency rule:** domain ← application ← infrastructure. The domain never imports Spring.

## Key Design Decisions

**Orchestrated Saga over Choreography** — a dedicated `orchestrator-service` owns the saga state machine, making the distributed transaction flow explicit, observable, and easier to debug. Each state transition is validated; invalid transitions throw `IllegalStateException`.

**Database-per-service** — each service owns its schema. No shared databases, no shared JPA entities across service boundaries.

**No type headers in Kafka** — `ADD_TYPE_INFO_HEADERS=false` on all producers. Deserialization is driven by the listener method parameter type via `JacksonJsonMessageConverter`, enabling cross-service contract flexibility.

**Saga items stored as JSON column** — `OrderSaga` persists item list as a `TEXT` column via `SagaItemsConverter` (`@Converter`), avoiding a separate join table for compensation data.
