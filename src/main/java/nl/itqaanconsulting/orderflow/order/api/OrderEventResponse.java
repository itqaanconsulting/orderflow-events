package nl.itqaanconsulting.orderflow.order.api;

import nl.itqaanconsulting.orderflow.order.domain.OrderEvent;
import nl.itqaanconsulting.orderflow.order.domain.OrderEventType;

import java.time.Instant;
import java.util.UUID;

public record OrderEventResponse(
        UUID id,
        UUID orderId,
        OrderEventType type,
        String message,
        Instant createdAt
) {
    public static OrderEventResponse from(OrderEvent event) {
        return new OrderEventResponse(
                event.getId(),
                event.getOrderId(),
                event.getType(),
                event.getMessage(),
                event.getCreatedAt()
        );
    }
}
