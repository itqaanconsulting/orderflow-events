package nl.itqaanconsulting.orderflow.order.persistence;

import nl.itqaanconsulting.orderflow.order.domain.ProcessedMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, UUID> {
}
