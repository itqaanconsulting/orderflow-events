package nl.itqaanconsulting.orderflow.order;

import java.util.UUID;

public record OrderProcessingResponse(
        UUID orderId,
        OrderStatus currentStatus,
        String message
) {
}
