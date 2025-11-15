package nl.rabobank.mongo.repository;

import lombok.RequiredArgsConstructor;
import lombok.val;
import nl.rabobank.audit.AuditLog;
import nl.rabobank.mongo.client.AuditLogMongoClient;
import nl.rabobank.mongo.mapper.AuditLogMapper;
import nl.rabobank.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogRepositoryImpl implements AuditLogRepository {

    private final AuditLogMongoClient client;

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
