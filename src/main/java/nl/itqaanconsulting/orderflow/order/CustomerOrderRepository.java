package nl.itqaanconsulting.orderflow.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, UUID> {

    boolean existsByExternalReference(String externalReference);

    Optional<CustomerOrder> findByExternalReference(String externalReference);
}
