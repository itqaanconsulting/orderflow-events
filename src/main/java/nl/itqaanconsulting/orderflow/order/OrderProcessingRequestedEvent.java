package nl.itqaanconsulting.orderflow.order;

import java.util.UUID;

public record OrderProcessingRequestedEvent(UUID orderId) {
}
