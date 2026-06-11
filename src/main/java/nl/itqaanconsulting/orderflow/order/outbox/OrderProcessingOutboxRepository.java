package nl.itqaanconsulting.orderflow.order.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderProcessingOutboxRepository extends JpaRepository<OrderProcessingOutbox, UUID> {

    List<OrderProcessingOutbox> findTop20ByStatusOrderByCreatedAt(OutboxStatus status);

    List<OrderProcessingOutbox> findAllByOrderByCreatedAtDesc();
}
