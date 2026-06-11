package nl.itqaanconsulting.orderflow.order.outbox;

import nl.itqaanconsulting.orderflow.order.messaging.OrderKafkaProperties;
import nl.itqaanconsulting.orderflow.order.messaging.OrderProcessingRequestedEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(name = "orderflow.messaging.mode", havingValue = "kafka", matchIfMissing = true)
public class KafkaOutboxPublisher {

    private final OrderProcessingOutboxRepository repository;
    private final KafkaTemplate<String, OrderProcessingRequestedEvent> kafkaTemplate;
    private final OrderKafkaProperties properties;

    public KafkaOutboxPublisher(
            OrderProcessingOutboxRepository repository,
            KafkaTemplate<String, OrderProcessingRequestedEvent> kafkaTemplate,
            OrderKafkaProperties properties
    ) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
    }

    @Scheduled(fixedDelayString = "${orderflow.outbox.publish-interval-ms:500}")
    @Transactional
    public void publishPending() {
        for (OrderProcessingOutbox entry :
                repository.findTop20ByStatusOrderByCreatedAt(OutboxStatus.PENDING)) {
            try {
                OrderProcessingRequestedEvent event =
                        new OrderProcessingRequestedEvent(entry.getMessageId(), entry.getOrderId());
                kafkaTemplate.send(
                                properties.getOrderProcessingTopic(),
                                entry.getOrderId().toString(),
                                event
                        )
                        .get(5, TimeUnit.SECONDS);
                entry.markPublished();
            } catch (Exception exception) {
                entry.recordFailure(exception);
            }
        }
    }
}
