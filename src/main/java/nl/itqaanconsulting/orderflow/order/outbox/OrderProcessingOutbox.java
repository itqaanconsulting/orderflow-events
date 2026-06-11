package nl.itqaanconsulting.orderflow.order.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "order_processing_outbox")
public class OrderProcessingOutbox {

    @Id
    @Column(name = "message_id")
    private UUID messageId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OutboxStatus status;

    @Column(nullable = false)
    private int attempts;

    @Column(name = "last_error", length = 1000)
    private String lastError;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    protected OrderProcessingOutbox() {
    }

    public OrderProcessingOutbox(UUID messageId, UUID orderId) {
        this.messageId = messageId;
        this.orderId = orderId;
        this.status = OutboxStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public void markPublished() {
        attempts++;
        status = OutboxStatus.PUBLISHED;
        publishedAt = Instant.now();
        lastError = null;
    }

    public void recordFailure(Throwable exception) {
        attempts++;
        lastError = exception.getMessage();
    }

    public UUID getMessageId() {
        return messageId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public OutboxStatus getStatus() {
        return status;
    }

    public int getAttempts() {
        return attempts;
    }

    public String getLastError() {
        return lastError;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }
}
