package nl.itqaanconsulting.orderflow.order;

public enum OrderEventType {
    ORDER_RECEIVED,
    ORDER_PROCESSING_REQUESTED,
    ORDER_VALIDATED,
    PAYMENT_CAPTURED,
    INVENTORY_RESERVED,
    SHIPMENT_PREPARED
}
