package nl.itqaanconsulting.orderflow.order.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("kafka-it")
class KafkaOrderProcessingIT {

    @Container
    static final KafkaContainer KAFKA = new KafkaContainer("apache/kafka-native:3.8.0");

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    }

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    KafkaOrderProcessingIT(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    void processesOrderThroughKafka() throws Exception {
        UUID orderId = createOrder("ORD-KAFKA-2001");

        mockMvc.perform(post("/api/orders/{id}/process", orderId))
                .andExpect(status().isAccepted());

        await().atMost(Duration.ofSeconds(15)).untilAsserted(() ->
                mockMvc.perform(get("/api/orders/{id}", orderId))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value("READY_TO_SHIP"))
        );

        mockMvc.perform(get("/api/orders/{id}/events", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[5].type").value("SHIPMENT_PREPARED"));
    }

    @Test
    void publishesFailedOrderToDeadLetterTopic() throws Exception {
        UUID orderId = createOrder("ORD-FAIL-INVENTORY-KAFKA-2002");
        JsonNode processingResponse = objectMapper.readTree(
                mockMvc.perform(post("/api/orders/{id}/process", orderId))
                        .andExpect(status().isAccepted())
                        .andReturn()
                        .getResponse()
                        .getContentAsString()
        );

        await().atMost(Duration.ofSeconds(15)).untilAsserted(() ->
                mockMvc.perform(get("/api/orders/{id}", orderId))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value("PROCESSING_FAILED"))
        );

        try (Consumer<String, String> consumer = deadLetterConsumer()) {
            consumer.subscribe(java.util.List.of("order-processing-requests-dlt"));
            ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(
                    consumer,
                    "order-processing-requests-dlt",
                    Duration.ofSeconds(15)
            );

            assertThat(record.key()).isEqualTo(orderId.toString());
            assertThat(record.value()).contains(orderId.toString());
            assertThat(record.value()).contains(processingResponse.get("messageId").asText());
        }
    }

    private UUID createOrder(String externalReference) throws Exception {
        JsonNode response = objectMapper.readTree(
                mockMvc.perform(post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "externalReference": "%s",
                                          "customerEmail": "kafka-it@example.com",
                                          "totalAmount": 249.95,
                                          "currency": "EUR"
                                        }
                                        """.formatted(externalReference)))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString()
        );
        return UUID.fromString(response.get("id").asText());
    }

    private Consumer<String, String> deadLetterConsumer() {
        Map<String, Object> properties = KafkaTestUtils.consumerProps(
                KAFKA.getBootstrapServers(),
                "orderflow-dlt-test-" + UUID.randomUUID(),
                "true"
        );
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(
                properties,
                new StringDeserializer(),
                new StringDeserializer()
        ).createConsumer();
    }
}
