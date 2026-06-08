package nl.itqaanconsulting.orderflow.order.messaging;

import nl.itqaanconsulting.orderflow.order.application.OrderService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@ConditionalOnProperty(name = "orderflow.messaging.mode", havingValue = "kafka", matchIfMissing = true)
public class KafkaErrorHandlingConfig {

    @Bean
    DefaultErrorHandler orderProcessingErrorHandler(
            KafkaTemplate<String, OrderProcessingRequestedEvent> kafkaTemplate,
            OrderKafkaProperties properties,
            OrderService orderService
    ) {
        ConsumerRecordRecoverer recoverer = (record, exception) ->
                recover(record, exception, kafkaTemplate, properties, orderService);

        return new DefaultErrorHandler(
                recoverer,
                new FixedBackOff(properties.getRetryIntervalMs(), properties.getMaxRetries())
        );
    }

    private void recover(
            ConsumerRecord<?, ?> record,
            Exception exception,
            KafkaTemplate<String, OrderProcessingRequestedEvent> kafkaTemplate,
            OrderKafkaProperties properties,
            OrderService orderService
    ) {
        if (!(record.value() instanceof OrderProcessingRequestedEvent event)) {
            return;
        }

        orderService.markProcessingFailed(event.orderId(), rootMessage(exception));
        kafkaTemplate.send(properties.getDeadLetterTopic(), event.orderId().toString(), event);
    }

    private String rootMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage();
    }
}
