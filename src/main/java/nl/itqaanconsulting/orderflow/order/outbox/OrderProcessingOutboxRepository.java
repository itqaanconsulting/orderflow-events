package nl.itqaanconsulting.orderflow.order.outbox;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;
import java.util.UUID;

public interface OrderProcessingOutboxRepository extends JpaRepository<OrderProcessingOutbox, UUID> {

    /**
     * Claims a batch of entries with SELECT ... FOR UPDATE SKIP LOCKED (lock timeout -2),
     * so several application instances can poll the outbox without publishing an entry twice.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2"))
    List<OrderProcessingOutbox> findTop20ByStatusOrderByCreatedAt(OutboxStatus status);

    List<OrderProcessingOutbox> findAllByOrderByCreatedAtDesc();
}
