package nl.itqaanconsulting.orderflow.order.api;

import nl.itqaanconsulting.orderflow.order.domain.ProcessedMessage;

import java.time.Instant;
import java.util.UUID;

public record ProcessedMessageResponse(
        UUID messageId,
        UUID orderId,
        Instant processedAt
) {
    public static ProcessedMessageResponse from(ProcessedMessage message) {
        return new ProcessedMessageResponse(message.getMessageId(), message.getOrderId(), message.getProcessedAt());
    }
}
