package nl.itqaanconsulting.orderflow.order.messaging;

import java.util.UUID;

public record OrderProcessingRequestedEvent(UUID messageId, UUID orderId) {

    public OrderProcessingRequestedEvent(UUID orderId) {
        this(UUID.randomUUID(), orderId);
    }
}
