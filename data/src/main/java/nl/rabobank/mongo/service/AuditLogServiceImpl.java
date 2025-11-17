package nl.rabobank.mongo.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import nl.rabobank.audit.AuditLog;
import nl.rabobank.mongo.mapper.AuditLogMapper;
import nl.rabobank.mongo.repository.AuditLogMongoRepository;
import nl.rabobank.repository.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogMongoRepository client;

    @Override
    public AuditLog save(AuditLog auditLog) {
        val saved = client.save(AuditLogMapper.toDocument(auditLog));
        return AuditLogMapper.toDomain(saved);

    }

    @Override
    public Page<AuditLog> findByAccountNumber(String accountNumber, Pageable pageable) {
        val page = client.findByAccountNumber(accountNumber, pageable);
        return page.map(AuditLogMapper::toDomain);
    }

    @Override
    public Page<AuditLog> findByActorName(String actorName, Pageable pageable) {
        val page = client.findByActorName(actorName, pageable);
        return page.map(AuditLogMapper::toDomain);
    }
}
