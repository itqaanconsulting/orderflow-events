package nl.itqaanconsulting.orderflow.order.domain;

public enum OrderStatus {
    RECEIVED,
    VALIDATED,
    PAID,
    INVENTORY_RESERVED,
    READY_TO_SHIP,
    PROCESSING_FAILED
}
