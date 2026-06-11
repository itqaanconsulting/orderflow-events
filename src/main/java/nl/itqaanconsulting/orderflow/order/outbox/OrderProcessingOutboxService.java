package nl.itqaanconsulting.orderflow.order.outbox;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrderProcessingOutboxService {

    private final OrderProcessingOutboxRepository repository;

    public OrderProcessingOutboxService(OrderProcessingOutboxRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void enqueue(UUID messageId, UUID orderId) {
        repository.save(new OrderProcessingOutbox(messageId, orderId));
    }

    @Transactional(readOnly = true)
    public List<OutboxResponse> findAll() {
        return repository.findAllByOrderByCreatedAtDesc().stream()
                .map(OutboxResponse::from)
                .toList();
    }
}
