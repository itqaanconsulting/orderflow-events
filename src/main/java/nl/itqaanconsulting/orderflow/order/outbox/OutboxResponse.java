package nl.itqaanconsulting.orderflow.order.outbox;

import java.time.Instant;
import java.util.UUID;

public record OutboxResponse(
        UUID messageId,
        UUID orderId,
        OutboxStatus status,
        int attempts,
        String lastError,
        Instant createdAt,
        Instant publishedAt
) {

    static OutboxResponse from(OrderProcessingOutbox entry) {
        return new OutboxResponse(
                entry.getMessageId(),
                entry.getOrderId(),
                entry.getStatus(),
                entry.getAttempts(),
                entry.getLastError(),
                entry.getCreatedAt(),
                entry.getPublishedAt()
        );
    }
}
