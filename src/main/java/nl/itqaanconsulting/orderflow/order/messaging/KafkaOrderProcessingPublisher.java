package nl.itqaanconsulting.orderflow.order.messaging;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "orderflow.messaging.mode", havingValue = "kafka", matchIfMissing = true)
public class KafkaOrderProcessingPublisher implements OrderProcessingPublisher {

    private final KafkaTemplate<String, OrderProcessingRequestedEvent> kafkaTemplate;
    private final OrderKafkaProperties properties;

    public KafkaOrderProcessingPublisher(
            KafkaTemplate<String, OrderProcessingRequestedEvent> kafkaTemplate,
            OrderKafkaProperties properties
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
    }

    @Override
    public void publish(OrderProcessingRequestedEvent event) {
        kafkaTemplate.send(properties.getOrderProcessingTopic(), event.orderId().toString(), event);
    }
}
