package nl.itqaanconsulting.orderflow.order.messaging;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableConfigurationProperties(OrderKafkaProperties.class)
public class KafkaTopicConfig {

    @Bean
    @ConditionalOnProperty(name = "orderflow.messaging.mode", havingValue = "kafka", matchIfMissing = true)
    NewTopic orderProcessingTopic(OrderKafkaProperties properties) {
        return TopicBuilder.name(properties.getOrderProcessingTopic())
                .partitions(3)
                .replicas(1)
                .build();
    }
}
