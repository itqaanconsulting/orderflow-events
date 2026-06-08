package nl.itqaanconsulting.orderflow.order.application;

import nl.itqaanconsulting.orderflow.order.domain.OrderProcessingException;
import nl.itqaanconsulting.orderflow.order.domain.OrderStatus;
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
        if (processedMessageService.isProcessed(event.messageId())) {
            return;
        }

        OrderStatus status = orderService.findById(event.orderId()).status();
        if (status == OrderStatus.RECEIVED) {
            status = orderService.validate(event.orderId()).status();
        }
        if (status == OrderStatus.VALIDATED) {
            status = orderService.markPaid(event.orderId()).status();
        }
        if (status == OrderStatus.PAID) {
            if (orderService.shouldFailInventoryReservation(event.orderId())) {
                throw new OrderProcessingException("Inventory reservation failed for demo scenario.");
            }
            status = orderService.reserveInventory(event.orderId()).status();
        }
        if (status == OrderStatus.INVENTORY_RESERVED) {
            orderService.prepareShipment(event.orderId());
        }

        processedMessageService.markProcessed(event.messageId(), event.orderId());
    }
}
