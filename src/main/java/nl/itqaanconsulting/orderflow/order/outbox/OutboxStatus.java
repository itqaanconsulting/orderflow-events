package nl.itqaanconsulting.orderflow.order.outbox;

public enum OutboxStatus {
    PENDING,
    PUBLISHED,
    /** Gave up after the configured number of attempts; needs manual attention. */
    FAILED
}
