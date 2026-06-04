package nl.itqaanconsulting.orderflow.order;

public interface OrderProcessingPublisher {

    void publish(OrderProcessingRequestedEvent event);
}
