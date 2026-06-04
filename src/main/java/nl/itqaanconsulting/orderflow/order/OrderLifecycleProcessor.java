package nl.itqaanconsulting.orderflow.order;

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
        orderService.validate(event.orderId());
        orderService.markPaid(event.orderId());
        orderService.reserveInventory(event.orderId());
        orderService.prepareShipment(event.orderId());
    }
}
