package nl.rabobank.service;

import lombok.RequiredArgsConstructor;
import nl.rabobank.audit.AuditLog;
import nl.rabobank.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetActorAuditLogsService {

    private final AuditLogRepository auditLogRepository;

    public Page<AuditLog> listByActor(String actorName, Pageable pageable) {
        return auditLogRepository.findByActorName(actorName, pageable);
    }
}
