package nl.itqaanconsulting.orderflow.order.api;

import nl.itqaanconsulting.orderflow.order.domain.OrderStatus;

import java.util.UUID;

public record OrderProcessingResponse(
        UUID messageId,
        UUID orderId,
        OrderStatus currentStatus,
        String message
) {
}
