package nl.itqaanconsulting.orderflow.order.application;

import nl.itqaanconsulting.orderflow.order.api.CreateOrderRequest;
import nl.itqaanconsulting.orderflow.order.api.OrderEventResponse;
import nl.itqaanconsulting.orderflow.order.api.OrderResponse;
import nl.itqaanconsulting.orderflow.order.domain.CustomerOrder;
import nl.itqaanconsulting.orderflow.order.domain.OrderEvent;
import nl.itqaanconsulting.orderflow.order.domain.OrderEventType;
import nl.itqaanconsulting.orderflow.order.domain.OrderStatus;
import nl.itqaanconsulting.orderflow.order.persistence.CustomerOrderRepository;
import nl.itqaanconsulting.orderflow.order.persistence.OrderEventRepository;
import nl.itqaanconsulting.orderflow.shared.BusinessRuleException;
import nl.itqaanconsulting.orderflow.shared.DuplicateResourceException;
import nl.itqaanconsulting.orderflow.shared.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final CustomerOrderRepository orderRepository;
    private final OrderEventRepository eventRepository;

    public OrderService(CustomerOrderRepository orderRepository, OrderEventRepository eventRepository) {
        this.orderRepository = orderRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        if (orderRepository.existsByExternalReference(request.externalReference())) {
            throw new DuplicateResourceException("Order already exists for reference: " + request.externalReference());
        }

        CustomerOrder order = new CustomerOrder(
                request.externalReference(),
                request.customerEmail(),
                request.totalAmount(),
                request.currency().toUpperCase()
        );

        CustomerOrder saved = orderRepository.save(order);
        appendEvent(saved, OrderEventType.ORDER_RECEIVED, "Order received from API.");
        return OrderResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(UUID id) {
        return OrderResponse.from(getOrder(id));
    }

    @Transactional(readOnly = true)
    public List<OrderEventResponse> findEvents(UUID orderId) {
        getOrder(orderId);
        return eventRepository.findByOrderIdOrderByCreatedAtAsc(orderId)
                .stream()
                .map(OrderEventResponse::from)
                .toList();
    }

    @Transactional
    public OrderResponse requestProcessing(UUID id) {
        CustomerOrder order = getOrder(id);
        if (order.getStatus() != OrderStatus.RECEIVED) {
            throw new BusinessRuleException("Order processing can only be requested for RECEIVED orders.");
        }
        appendEvent(order, OrderEventType.ORDER_PROCESSING_REQUESTED, "Order processing requested.");
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse validate(UUID id) {
        CustomerOrder order = getOrder(id);
        move(order, OrderStatus.RECEIVED, OrderStatus.VALIDATED, OrderEventType.ORDER_VALIDATED, "Order passed validation.");
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse markPaid(UUID id) {
        CustomerOrder order = getOrder(id);
        move(order, OrderStatus.VALIDATED, OrderStatus.PAID, OrderEventType.PAYMENT_CAPTURED, "Payment captured.");
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse reserveInventory(UUID id) {
        CustomerOrder order = getOrder(id);
        move(order, OrderStatus.PAID, OrderStatus.INVENTORY_RESERVED, OrderEventType.INVENTORY_RESERVED, "Inventory reserved.");
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse prepareShipment(UUID id) {
        CustomerOrder order = getOrder(id);
        move(order, OrderStatus.INVENTORY_RESERVED, OrderStatus.READY_TO_SHIP, OrderEventType.SHIPMENT_PREPARED, "Shipment prepared.");
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse markProcessingFailed(UUID id, String reason) {
        CustomerOrder order = getOrder(id);
        order.moveTo(OrderStatus.PROCESSING_FAILED);
        appendEvent(order, OrderEventType.PROCESSING_FAILED, "Processing failed: " + reason);
        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public boolean shouldFailInventoryReservation(UUID id) {
        CustomerOrder order = getOrder(id);
        return order.getExternalReference().contains("FAIL-INVENTORY");
    }

    private CustomerOrder getOrder(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
    }

    private void move(CustomerOrder order, OrderStatus expected, OrderStatus next, OrderEventType eventType, String message) {
        if (order.getStatus() != expected) {
            throw new BusinessRuleException("Order must be " + expected + " before moving to " + next + ".");
        }
        order.moveTo(next);
        appendEvent(order, eventType, message);
    }

    private void appendEvent(CustomerOrder order, OrderEventType type, String message) {
        eventRepository.save(new OrderEvent(order.getId(), type, message));
    }
}
