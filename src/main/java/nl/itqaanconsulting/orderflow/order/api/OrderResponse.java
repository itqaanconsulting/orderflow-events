package nl.itqaanconsulting.orderflow.order.api;

import nl.itqaanconsulting.orderflow.order.domain.CustomerOrder;
import nl.itqaanconsulting.orderflow.order.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String externalReference,
        String customerEmail,
        BigDecimal totalAmount,
        String currency,
        OrderStatus status,
        Instant createdAt,
        Instant updatedAt
) {
    public static OrderResponse from(CustomerOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getExternalReference(),
                order.getCustomerEmail(),
                order.getTotalAmount(),
                order.getCurrency(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
