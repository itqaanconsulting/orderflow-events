package nl.itqaanconsulting.orderflow.order.outbox;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders/outbox")
class OutboxController {

    private final OrderProcessingOutboxService service;

    OutboxController(OrderProcessingOutboxService service) {
        this.service = service;
    }

    @GetMapping
    List<OutboxResponse> findAll() {
        return service.findAll();
    }
}
