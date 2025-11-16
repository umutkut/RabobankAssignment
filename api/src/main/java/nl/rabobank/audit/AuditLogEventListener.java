package nl.rabobank.audit;

import lombok.RequiredArgsConstructor;
import nl.rabobank.repository.AuditLogRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class AuditLogEventListener {
    private final AuditLogRepository auditLogRepository;

    @Async("auditExecutor")
    @EventListener
    public void listenAuditLogEvent(AuditLogEvent event) {
        auditLogRepository.save(event.auditLog());
    }
}
