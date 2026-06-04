# OrderFlow Events

Event-driven order processing showcase built with Java 21, Spring Boot, Kafka and PostgreSQL.

The project models an order lifecycle where the API accepts work, Kafka decouples processing, and a persisted event log records every workflow step.

## What it demonstrates

- Spring Boot REST API
- Order lifecycle modeling
- Event log per aggregate
- Kafka-backed async order processing
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
GET /api/orders/{id}/events
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

The endpoint returns `202 Accepted`. The Kafka consumer then moves the order to `READY_TO_SHIP` and records the lifecycle events.

## Test

```powershell
mvn test
```

## Current scope

This version uses a persisted event log, explicit workflow endpoints and Kafka-backed async processing for `POST /api/orders/{id}/process`. Tests use an in-memory publisher so the suite stays fast and does not require Docker.
