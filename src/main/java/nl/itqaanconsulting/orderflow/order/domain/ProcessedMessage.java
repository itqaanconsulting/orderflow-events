package nl.itqaanconsulting.orderflow.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "processed_messages")
public class ProcessedMessage {

    @Id
    private UUID messageId;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private Instant processedAt;

    protected ProcessedMessage() {
    }

    public ProcessedMessage(UUID messageId, UUID orderId) {
        this.messageId = messageId;
        this.orderId = orderId;
        this.processedAt = Instant.now();
    }

    public UUID getMessageId() {
        return messageId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }
}
