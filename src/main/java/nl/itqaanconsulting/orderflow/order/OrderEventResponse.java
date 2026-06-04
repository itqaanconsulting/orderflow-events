package nl.itqaanconsulting.orderflow.order;

import java.time.Instant;
import java.util.UUID;

public record OrderEventResponse(
        UUID id,
        UUID orderId,
        OrderEventType type,
        String message,
        Instant createdAt
) {
    static OrderEventResponse from(OrderEvent event) {
        return new OrderEventResponse(
                event.getId(),
                event.getOrderId(),
                event.getType(),
                event.getMessage(),
                event.getCreatedAt()
        );
    }
}
