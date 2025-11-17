package nl.rabobank.service;

import lombok.RequiredArgsConstructor;
import nl.rabobank.audit.AuditLog;
import nl.rabobank.repository.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetActorAuditLogsService {

    private final AuditLogService auditLogService;

    public Page<AuditLog> listByActor(String actorName, Pageable pageable) {
        return auditLogService.findByActorName(actorName, pageable);
    }
}
