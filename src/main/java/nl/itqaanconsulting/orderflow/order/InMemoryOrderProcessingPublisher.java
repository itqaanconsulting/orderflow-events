package nl.itqaanconsulting.orderflow.order;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "orderflow.messaging.mode", havingValue = "in-memory")
public class InMemoryOrderProcessingPublisher implements OrderProcessingPublisher {

    private final OrderLifecycleProcessor orderLifecycleProcessor;

    public InMemoryOrderProcessingPublisher(OrderLifecycleProcessor orderLifecycleProcessor) {
        this.orderLifecycleProcessor = orderLifecycleProcessor;
    }

    @Async
    @Override
    public void publish(OrderProcessingRequestedEvent event) {
        orderLifecycleProcessor.process(event);
    }
}
