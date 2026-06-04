package nl.itqaanconsulting.orderflow.order.domain;

public class OrderProcessingException extends RuntimeException {

    public OrderProcessingException(String message) {
        super(message);
    }
}
