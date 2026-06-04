package nl.itqaanconsulting.orderflow.order.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "orderflow.kafka")
public class OrderKafkaProperties {

    private String orderProcessingTopic = "order-processing-requests";
    private String consumerGroup = "orderflow-events";

    public String getOrderProcessingTopic() {
        return orderProcessingTopic;
    }

    public void setOrderProcessingTopic(String orderProcessingTopic) {
        this.orderProcessingTopic = orderProcessingTopic;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }
}
