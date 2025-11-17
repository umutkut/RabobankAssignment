package nl.rabobank.audit;

import lombok.RequiredArgsConstructor;
import nl.rabobank.repository.AuditLogService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class AuditLogEventListener {
    private final AuditLogService auditLogService;

    @Async("auditExecutor")
    @EventListener
    public void listenAuditLogEvent(AuditLogEvent event) {
        auditLogService.save(event.auditLog());
    }
}
