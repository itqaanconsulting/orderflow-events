package nl.itqaanconsulting.orderflow.order;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderProcessingService {

    private final OrderService orderService;
    private final ApplicationEventPublisher eventPublisher;

    public OrderProcessingService(OrderService orderService, ApplicationEventPublisher eventPublisher) {
        this.orderService = orderService;
        this.eventPublisher = eventPublisher;
    }

    public OrderProcessingResponse requestProcessing(UUID orderId) {
        OrderResponse order = orderService.requestProcessing(orderId);
        eventPublisher.publishEvent(new OrderProcessingRequestedEvent(orderId));
        return new OrderProcessingResponse(order.id(), order.status(), "Order processing requested.");
    }

    @Async
    @EventListener
    public void process(OrderProcessingRequestedEvent event) {
        orderService.validate(event.orderId());
        orderService.markPaid(event.orderId());
        orderService.reserveInventory(event.orderId());
        orderService.prepareShipment(event.orderId());
    }
}
