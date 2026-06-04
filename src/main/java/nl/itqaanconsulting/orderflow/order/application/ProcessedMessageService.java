package nl.itqaanconsulting.orderflow.order.application;

import nl.itqaanconsulting.orderflow.order.api.ProcessedMessageResponse;
import nl.itqaanconsulting.orderflow.order.domain.ProcessedMessage;
import nl.itqaanconsulting.orderflow.order.persistence.ProcessedMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProcessedMessageService {

    private final ProcessedMessageRepository repository;

    public ProcessedMessageService(ProcessedMessageRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public boolean claim(UUID messageId, UUID orderId) {
        if (repository.existsById(messageId)) {
            return false;
        }
        repository.save(new ProcessedMessage(messageId, orderId));
        return true;
    }

    @Transactional(readOnly = true)
    public List<ProcessedMessageResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(ProcessedMessageResponse::from)
                .toList();
    }
}
