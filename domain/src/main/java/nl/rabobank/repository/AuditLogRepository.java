package nl.rabobank.repository;

import nl.rabobank.audit.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogRepository {
    AuditLog save(AuditLog auditLog);

    Page<AuditLog> findByAccountNumber(String accountNumber, Pageable pageable);

    Page<AuditLog> findByActorName(String actorName, Pageable pageable);
}
