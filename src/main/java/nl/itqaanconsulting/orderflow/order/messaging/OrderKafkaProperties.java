package nl.itqaanconsulting.orderflow.order.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "orderflow.kafka")
public class OrderKafkaProperties {

    private String orderProcessingTopic = "order-processing-requests";
    private String deadLetterTopic = "order-processing-requests-dlt";
    private String consumerGroup = "orderflow-events";
    private long retryIntervalMs = 1000;
    private long maxRetries = 2;

    public String getOrderProcessingTopic() {
        return orderProcessingTopic;
    }

    public void setOrderProcessingTopic(String orderProcessingTopic) {
        this.orderProcessingTopic = orderProcessingTopic;
    }

    public String getDeadLetterTopic() {
        return deadLetterTopic;
    }

    public void setDeadLetterTopic(String deadLetterTopic) {
        this.deadLetterTopic = deadLetterTopic;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public long getRetryIntervalMs() {
        return retryIntervalMs;
    }

    public void setRetryIntervalMs(long retryIntervalMs) {
        this.retryIntervalMs = retryIntervalMs;
    }

    public long getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(long maxRetries) {
        this.maxRetries = maxRetries;
    }
}
