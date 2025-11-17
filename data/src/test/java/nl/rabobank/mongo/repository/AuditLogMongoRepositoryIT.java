package nl.rabobank.mongo.repository;

import lombok.val;
import nl.rabobank.audit.AuditLog;
import nl.rabobank.mongo.EmbeddedMongoConfiguration;
import nl.rabobank.mongo.mapper.AuditLogMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static nl.rabobank.mongo.TestUtils.CREATED_AT;
import static nl.rabobank.mongo.TestUtils.givenPowerOfAttorney;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = EmbeddedMongoConfiguration.class)
class AuditLogMongoRepositoryIT {

    @Autowired
    private AuditLogMongoRepository auditLogMongoRepository;

    @BeforeEach
    void clean() {
        auditLogMongoRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveAuditLogDocument() {
        // Given
        val poa = givenPowerOfAttorney();
        val audit = AuditLog.created("audit-id", CREATED_AT, poa);

        // When
        val result = auditLogMongoRepository.save(AuditLogMapper.toDocument(audit));
        val retrieved = auditLogMongoRepository.findById(result.getId());

        // Then
        assertTrue(retrieved.isPresent());
        val savedLog = retrieved.get();
        assertEquals(audit, AuditLogMapper.toDomain(savedLog));
    }

    @Test
    void shouldFindByAccountNumberWithPagination() {
        // Given
        val poa = givenPowerOfAttorney();
        val audit1 = AuditLog.created("audit-acc-1", CREATED_AT, poa);
        val audit2 = AuditLog.created("audit-acc-2", CREATED_AT.plusSeconds(10), poa);
        auditLogMongoRepository.save(AuditLogMapper.toDocument(audit1));
        auditLogMongoRepository.save(AuditLogMapper.toDocument(audit2));

        // When
        val page = auditLogMongoRepository.findByAccountNumber(poa.account().accountNumber(), PageRequest.of(0, 10));

        // Then
        assertEquals(2, page.getTotalElements());
        val ids = page.map(AuditLogMapper::toDomain).map(AuditLog::id).toList();
        assertTrue(ids.contains("audit-acc-1"));
        assertTrue(ids.contains("audit-acc-2"));
    }

    @Test
    void shouldFindByActorNameWithPagination() {
        // Given
        val poa = givenPowerOfAttorney();
        val base = AuditLog.created("audit-actor-1", CREATED_AT, poa);
        // same actorName as TestUtils.ACTOR inside mapper when created via factory
        auditLogMongoRepository.save(AuditLogMapper.toDocument(base));

        // When
        val page = auditLogMongoRepository.findByActorName(base.actorName(), PageRequest.of(0, 5));

        // Then
        assertTrue(page.getTotalElements() >= 1);
        val first = page.getContent().getFirst();
        assertEquals(base.actorName(), first.getActorName());
    }
}