package nl.itqaanconsulting.orderflow.order.messaging;

public interface OrderProcessingPublisher {

    void publish(OrderProcessingRequestedEvent event);
}
