# OrderFlow Events

Event-driven order processing showcase built with Java 21, Spring Boot, Kafka and PostgreSQL.

The project models an order lifecycle where the API accepts work, Kafka decouples processing, and a persisted event log records every workflow step.

## What it demonstrates

- Spring Boot REST API
- Order lifecycle modeling
- Event log per aggregate
- Kafka-backed async order processing
- Idempotent consumer handling for duplicate Kafka messages
- PostgreSQL schema migrations with Flyway
- Request validation and API error handling
- Integration tests with H2
- OpenAPI documentation

## Run locally

```powershell
docker compose up -d
mvn spring-boot:run
```

Docker starts PostgreSQL on `localhost:5433` and Kafka on `localhost:9092`.

The application publishes order processing requests to this topic:

```text
order-processing-requests
```

Swagger UI:

```text
http://localhost:8082/swagger-ui.html
```

Actuator:

```text
http://localhost:8082/actuator/health
http://localhost:8082/actuator/info
```

## API

```http
POST /api/orders
GET /api/orders
GET /api/orders/{id}
POST /api/orders/{id}/validate
POST /api/orders/{id}/mark-paid
POST /api/orders/{id}/reserve-inventory
POST /api/orders/{id}/prepare-shipment
POST /api/orders/{id}/process
POST /api/orders/{id}/process/{messageId}/replay
GET /api/orders/{id}/events
GET /api/orders/processed-messages
```

## Package structure

```text
order.api          REST controllers and DTOs
order.application  use cases and workflow services
order.domain       entities, enums and domain exceptions
order.messaging    Kafka producers, consumers and message contracts
order.persistence  Spring Data repositories
shared             shared API error handling
```

## Demo flow

Create an order:

```http
POST /api/orders
```

Request async processing:

```http
POST /api/orders/{id}/process
```

The endpoint returns `202 Accepted` with a `messageId`. The Kafka consumer then moves the order to `READY_TO_SHIP` and records the lifecycle events.

Replay the same processing message:

```http
POST /api/orders/{id}/process/{messageId}/replay
```

The first call processes the order. A second call with the same `messageId` is ignored by the idempotency guard, so the event log does not get duplicate lifecycle events.

Demo a processing failure by creating an order with an external reference that contains `FAIL-INVENTORY`, then call:

```http
POST /api/orders/{id}/process
```

The processor stops before inventory reservation, moves the order to `PROCESSING_FAILED`, and records a `PROCESSING_FAILED` event with the reason.

## Test

```powershell
mvn test
```

## Current scope

This version uses a persisted event log, explicit workflow endpoints, Kafka-backed async processing for `POST /api/orders/{id}/process`, and a `processed_messages` table to prevent duplicate message handling. Tests use an in-memory publisher so the suite stays fast and does not require Docker.
