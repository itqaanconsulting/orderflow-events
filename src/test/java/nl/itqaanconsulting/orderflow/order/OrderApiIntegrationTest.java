package nl.itqaanconsulting.orderflow.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderApiIntegrationTest {

    private final MockMvc mockMvc;

    @Autowired
    OrderApiIntegrationTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void createsOrderAndRecordsInitialEvent() throws Exception {
        UUID orderId = createOrder("ORD-1001");

        mockMvc.perform(get("/api/orders/{id}/events", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("ORDER_RECEIVED"));
    }

    @Test
    void processesOrderLifecycleInSequence() throws Exception {
        UUID orderId = createOrder("ORD-1002");

        mockMvc.perform(post("/api/orders/{id}/validate", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VALIDATED"));

        mockMvc.perform(post("/api/orders/{id}/mark-paid", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));

        mockMvc.perform(post("/api/orders/{id}/reserve-inventory", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INVENTORY_RESERVED"));

        mockMvc.perform(post("/api/orders/{id}/prepare-shipment", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READY_TO_SHIP"));

        mockMvc.perform(get("/api/orders/{id}/events", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[4].type").value("SHIPMENT_PREPARED"));
    }

    @Test
    void processesOrderAsynchronouslyWhenRequested() throws Exception {
        UUID orderId = createOrder("ORD-1005");

        mockMvc.perform(post("/api/orders/{id}/process", orderId))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.currentStatus").value("RECEIVED"))
                .andExpect(jsonPath("$.message").value("Order processing requested."));

        await().untilAsserted(() ->
                mockMvc.perform(get("/api/orders/{id}", orderId))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value("READY_TO_SHIP"))
        );

        mockMvc.perform(get("/api/orders/{id}/events", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[1].type").value("ORDER_PROCESSING_REQUESTED"))
                .andExpect(jsonPath("$[5].type").value("SHIPMENT_PREPARED"));
    }

    @Test
    void rejectsOutOfOrderLifecycleStep() throws Exception {
        UUID orderId = createOrder("ORD-1003");

        mockMvc.perform(post("/api/orders/{id}/mark-paid", orderId))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Order must be VALIDATED before moving to PAID."));
    }

    @Test
    void rejectsDuplicateExternalReference() throws Exception {
        createOrder("ORD-1004");

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "externalReference": "ORD-1004",
                                  "customerEmail": "customer@example.com",
                                  "totalAmount": 149.95,
                                  "currency": "EUR"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Order already exists for reference: ORD-1004"));
    }

    private UUID createOrder(String externalReference) throws Exception {
        String response = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "externalReference": "%s",
                                  "customerEmail": "customer@example.com",
                                  "totalAmount": 149.95,
                                  "currency": "EUR"
                                }
                                """.formatted(externalReference)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.externalReference").value(externalReference))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = response.substring(response.indexOf("\"id\":\"") + 6);
        id = id.substring(0, id.indexOf('"'));
        assertThat(id).isNotBlank();
        return UUID.fromString(id);
    }
}
