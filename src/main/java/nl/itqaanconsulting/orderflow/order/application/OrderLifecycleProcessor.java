package nl.itqaanconsulting.orderflow.order.application;

import nl.itqaanconsulting.orderflow.order.domain.OrderProcessingException;
import nl.itqaanconsulting.orderflow.order.messaging.OrderProcessingRequestedEvent;
import org.springframework.stereotype.Service;

@Service
public class OrderLifecycleProcessor {

    private final OrderService orderService;
    private final ProcessedMessageService processedMessageService;

    public OrderLifecycleProcessor(OrderService orderService, ProcessedMessageService processedMessageService) {
        this.orderService = orderService;
        this.processedMessageService = processedMessageService;
    }

    public void process(OrderProcessingRequestedEvent event) {
        if (!processedMessageService.claim(event.messageId(), event.orderId())) {
            return;
        }
        try {
            orderService.validate(event.orderId());
            orderService.markPaid(event.orderId());
            if (orderService.shouldFailInventoryReservation(event.orderId())) {
                throw new OrderProcessingException("Inventory reservation failed for demo scenario.");
            }
            orderService.reserveInventory(event.orderId());
            orderService.prepareShipment(event.orderId());
        } catch (RuntimeException exception) {
            orderService.markProcessingFailed(event.orderId(), exception.getMessage());
        }
    }
}
