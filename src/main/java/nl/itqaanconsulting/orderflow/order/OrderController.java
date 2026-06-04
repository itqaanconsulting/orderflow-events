package nl.itqaanconsulting.orderflow.order;

import jakarta.validation.Valid;
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

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
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

    @GetMapping("/{id}/events")
    public List<OrderEventResponse> findEvents(@PathVariable UUID id) {
        return orderService.findEvents(id);
    }
}
