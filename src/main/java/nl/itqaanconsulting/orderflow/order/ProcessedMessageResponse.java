package nl.itqaanconsulting.orderflow.order;

import java.time.Instant;
import java.util.UUID;

public record ProcessedMessageResponse(
        UUID messageId,
        UUID orderId,
        Instant processedAt
) {
    static ProcessedMessageResponse from(ProcessedMessage message) {
        return new ProcessedMessageResponse(message.getMessageId(), message.getOrderId(), message.getProcessedAt());
    }
}
