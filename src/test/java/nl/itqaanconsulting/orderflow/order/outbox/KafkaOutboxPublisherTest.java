package nl.itqaanconsulting.orderflow.order.outbox;

import nl.itqaanconsulting.orderflow.order.messaging.OrderKafkaProperties;
import nl.itqaanconsulting.orderflow.order.messaging.OrderProcessingRequestedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KafkaOutboxPublisherTest {

    @SuppressWarnings("unchecked")
    private final KafkaTemplate<String, OrderProcessingRequestedEvent> kafkaTemplate = mock(KafkaTemplate.class);
    private final OrderProcessingOutboxRepository repository = mock(OrderProcessingOutboxRepository.class);

    @Test
    void marksEntryFailedAfterMaxAttemptsInsteadOfRetryingForever() {
        OrderProcessingOutbox entry = new OrderProcessingOutbox(UUID.randomUUID(), UUID.randomUUID());
        when(repository.findTop20ByStatusOrderByCreatedAt(OutboxStatus.PENDING)).thenReturn(List.of(entry));
        when(kafkaTemplate.send(anyString(), anyString(), any(OrderProcessingRequestedEvent.class)))
                .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("broker unavailable")));

        KafkaOutboxPublisher publisher = new KafkaOutboxPublisher(repository, kafkaTemplate, new OrderKafkaProperties(), 3);

        publisher.publishPending();
        publisher.publishPending();
        assertThat(entry.getStatus()).isEqualTo(OutboxStatus.PENDING);
        assertThat(entry.getAttempts()).isEqualTo(2);

        publisher.publishPending();
        assertThat(entry.getStatus()).isEqualTo(OutboxStatus.FAILED);
        assertThat(entry.getAttempts()).isEqualTo(3);
        assertThat(entry.getLastError()).contains("broker unavailable");
    }

    @Test
    void marksEntryPublishedWhenBrokerAcknowledges() {
        OrderProcessingOutbox entry = new OrderProcessingOutbox(UUID.randomUUID(), UUID.randomUUID());
        when(repository.findTop20ByStatusOrderByCreatedAt(OutboxStatus.PENDING)).thenReturn(List.of(entry));
        when(kafkaTemplate.send(anyString(), anyString(), any(OrderProcessingRequestedEvent.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        new KafkaOutboxPublisher(repository, kafkaTemplate, new OrderKafkaProperties(), 3).publishPending();

        assertThat(entry.getStatus()).isEqualTo(OutboxStatus.PUBLISHED);
        assertThat(entry.getAttempts()).isEqualTo(1);
    }
}
