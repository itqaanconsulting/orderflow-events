package nl.itqaanconsulting.orderflow.order;

import org.springframework.stereotype.Service;

@Service
public class OrderLifecycleProcessor {

    private final OrderService orderService;

    public OrderLifecycleProcessor(OrderService orderService) {
        this.orderService = orderService;
    }

    public void process(OrderProcessingRequestedEvent event) {
        orderService.validate(event.orderId());
        orderService.markPaid(event.orderId());
        orderService.reserveInventory(event.orderId());
        orderService.prepareShipment(event.orderId());
    }
}
