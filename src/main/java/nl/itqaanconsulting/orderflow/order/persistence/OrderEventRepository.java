package nl.itqaanconsulting.orderflow.order.persistence;

import nl.itqaanconsulting.orderflow.order.domain.OrderEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderEventRepository extends JpaRepository<OrderEvent, UUID> {

    List<OrderEvent> findByOrderIdOrderByCreatedAtAsc(UUID orderId);
}
