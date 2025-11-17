package nl.rabobank.service;

import lombok.RequiredArgsConstructor;
import nl.rabobank.audit.AuditLog;
import nl.rabobank.repository.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAccountAuditLogsService {

    private final AuditLogService auditLogService;

    public Page<AuditLog> listByAccount(String accountNumber, Pageable pageable) {
        return auditLogService.findByAccountNumber(accountNumber, pageable);
    }
}
