package nl.itqaanconsulting.orderflow.order.application;

import nl.itqaanconsulting.orderflow.order.api.OrderProcessingResponse;
import nl.itqaanconsulting.orderflow.order.api.OrderResponse;
import nl.itqaanconsulting.orderflow.order.messaging.OrderProcessingPublisher;
import nl.itqaanconsulting.orderflow.order.messaging.OrderProcessingRequestedEvent;
import nl.itqaanconsulting.orderflow.order.outbox.OrderProcessingOutboxService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrderProcessingService {

    private final OrderService orderService;
    private final OrderProcessingPublisher publisher;
    private final OrderProcessingOutboxService outboxService;
    private final boolean kafkaMode;

    public OrderProcessingService(
            OrderService orderService,
            OrderProcessingPublisher publisher,
            OrderProcessingOutboxService outboxService,
            @Value("${orderflow.messaging.mode:kafka}") String messagingMode
    ) {
        this.orderService = orderService;
        this.publisher = publisher;
        this.outboxService = outboxService;
        this.kafkaMode = "kafka".equalsIgnoreCase(messagingMode);
    }

    @Transactional
    public OrderProcessingResponse requestProcessing(UUID orderId) {
        OrderResponse order = orderService.requestProcessing(orderId);
        OrderProcessingRequestedEvent event = new OrderProcessingRequestedEvent(orderId);
        if (kafkaMode) {
            outboxService.enqueue(event.messageId(), event.orderId());
        } else {
            publisher.publish(event);
        }
        return new OrderProcessingResponse(event.messageId(), order.id(), order.status(), "Order processing requested.");
    }

}
