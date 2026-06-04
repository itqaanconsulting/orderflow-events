package nl.itqaanconsulting.orderflow.order;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderProcessingService {

    private final OrderService orderService;
    private final OrderProcessingPublisher publisher;

    public OrderProcessingService(OrderService orderService, OrderProcessingPublisher publisher) {
        this.orderService = orderService;
        this.publisher = publisher;
    }

    public OrderProcessingResponse requestProcessing(UUID orderId) {
        OrderResponse order = orderService.requestProcessing(orderId);
        OrderProcessingRequestedEvent event = new OrderProcessingRequestedEvent(orderId);
        publisher.publish(event);
        return new OrderProcessingResponse(event.messageId(), order.id(), order.status(), "Order processing requested.");
    }

}
