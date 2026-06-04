# OrderFlow Events

Event-driven order processing showcase built with Java 21 and Spring Boot.

The first version focuses on a stable order domain, status lifecycle and an event log that records every workflow step. The next increment can add Kafka or RabbitMQ around this core without changing the public API.

## What it demonstrates

- Spring Boot REST API
- Order lifecycle modeling
- Event log per aggregate
- PostgreSQL schema migrations with Flyway
- Request validation and API error handling
- Integration tests with H2
- OpenAPI documentation

## Run locally

```powershell
docker compose up -d
mvn spring-boot:run
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
GET /api/orders/{id}/events
```

## Test

```powershell
mvn test
```

## Current scope

This version uses a persisted event log and explicit workflow endpoints. The next step is to introduce a message broker so lifecycle steps are processed by asynchronous consumers instead of direct endpoint calls.
