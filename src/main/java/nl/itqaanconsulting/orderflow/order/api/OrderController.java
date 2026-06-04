package nl.itqaanconsulting.orderflow.order.api;

import jakarta.validation.Valid;
import nl.itqaanconsulting.orderflow.order.application.OrderLifecycleProcessor;
import nl.itqaanconsulting.orderflow.order.application.OrderProcessingService;
import nl.itqaanconsulting.orderflow.order.application.OrderService;
import nl.itqaanconsulting.orderflow.order.application.ProcessedMessageService;
import nl.itqaanconsulting.orderflow.order.messaging.OrderProcessingRequestedEvent;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderProcessingService orderProcessingService;
    private final OrderLifecycleProcessor orderLifecycleProcessor;
    private final ProcessedMessageService processedMessageService;

    public OrderController(
            OrderService orderService,
            OrderProcessingService orderProcessingService,
            OrderLifecycleProcessor orderLifecycleProcessor,
            ProcessedMessageService processedMessageService
    ) {
        this.orderService = orderService;
        this.orderProcessingService = orderProcessingService;
        this.orderLifecycleProcessor = orderLifecycleProcessor;
        this.processedMessageService = processedMessageService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.create(request);
    }

    @GetMapping
    public List<OrderResponse> findAll() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public OrderResponse findById(@PathVariable UUID id) {
        return orderService.findById(id);
    }

    @PostMapping("/{id}/validate")
    public OrderResponse validate(@PathVariable UUID id) {
        return orderService.validate(id);
    }

    @PostMapping("/{id}/mark-paid")
    public OrderResponse markPaid(@PathVariable UUID id) {
        return orderService.markPaid(id);
    }

    @PostMapping("/{id}/reserve-inventory")
    public OrderResponse reserveInventory(@PathVariable UUID id) {
        return orderService.reserveInventory(id);
    }

    @PostMapping("/{id}/prepare-shipment")
    public OrderResponse prepareShipment(@PathVariable UUID id) {
        return orderService.prepareShipment(id);
    }

    @PostMapping("/{id}/process")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OrderProcessingResponse process(@PathVariable UUID id) {
        return orderProcessingService.requestProcessing(id);
    }

    @PostMapping("/{id}/process/{messageId}/replay")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OrderProcessingResponse replayProcessingMessage(@PathVariable UUID id, @PathVariable UUID messageId) {
        orderLifecycleProcessor.process(new OrderProcessingRequestedEvent(messageId, id));
        return new OrderProcessingResponse(messageId, id, orderService.findById(id).status(), "Processing message replayed.");
    }

    @GetMapping("/{id}/events")
    public List<OrderEventResponse> findEvents(@PathVariable UUID id) {
        return orderService.findEvents(id);
    }

    @GetMapping("/processed-messages")
    public List<ProcessedMessageResponse> findProcessedMessages() {
        return processedMessageService.findAll();
    }
}
