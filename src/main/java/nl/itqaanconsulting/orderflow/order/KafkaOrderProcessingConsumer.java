package nl.itqaanconsulting.orderflow.order;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "orderflow.messaging.mode", havingValue = "kafka", matchIfMissing = true)
public class KafkaOrderProcessingConsumer {

    private final OrderLifecycleProcessor orderLifecycleProcessor;

    public KafkaOrderProcessingConsumer(OrderLifecycleProcessor orderLifecycleProcessor) {
        this.orderLifecycleProcessor = orderLifecycleProcessor;
    }

    @KafkaListener(
            topics = "#{@orderKafkaProperties.orderProcessingTopic}",
            groupId = "#{@orderKafkaProperties.consumerGroup}"
    )
    public void consume(OrderProcessingRequestedEvent event) {
        orderLifecycleProcessor.process(event);
    }
}
