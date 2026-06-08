package nl.itqaanconsulting.orderflow.order.messaging;

import nl.itqaanconsulting.orderflow.order.application.OrderLifecycleProcessor;
import nl.itqaanconsulting.orderflow.order.application.OrderService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "orderflow.messaging.mode", havingValue = "in-memory")
public class InMemoryOrderProcessingPublisher implements OrderProcessingPublisher {

    private final OrderLifecycleProcessor orderLifecycleProcessor;
    private final OrderService orderService;
    private final OrderKafkaProperties properties;

    public InMemoryOrderProcessingPublisher(
            OrderLifecycleProcessor orderLifecycleProcessor,
            OrderService orderService,
            OrderKafkaProperties properties
    ) {
        this.orderLifecycleProcessor = orderLifecycleProcessor;
        this.orderService = orderService;
        this.properties = properties;
    }

    @Async
    @Override
    public void publish(OrderProcessingRequestedEvent event) {
        RuntimeException lastFailure = null;
        for (int attempt = 0; attempt <= properties.getMaxRetries(); attempt++) {
            try {
                orderLifecycleProcessor.process(event);
                return;
            } catch (RuntimeException exception) {
                lastFailure = exception;
            }
        }
        orderService.markProcessingFailed(event.orderId(), lastFailure.getMessage());
    }
}
