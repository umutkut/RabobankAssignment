package nl.rabobank.mongo.mapper;

import lombok.val;
import nl.rabobank.audit.UpdateType;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.mongo.documents.audit.UpdateTypeDocument;
import nl.rabobank.mongo.documents.poa.AuthorizationType;
import org.junit.jupiter.api.Test;

import static nl.rabobank.mongo.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class AuditLogMapperTest {

    @Test
    void shouldNotInstantiateUtilityClass() {
        assertThrows(IllegalStateException.class, AuditLogMapper::new);
    }

    @Test
    void toDocument_shouldMapAllFields() {
        // Given
        val auditLog = givenAuditLog();

        // When
        val doc = AuditLogMapper.toDocument(auditLog);

        // Then
        assertNotNull(doc);
        assertEquals(AUDIT_ID, doc.getId());
        assertEquals(ACTOR, doc.getActorName());
        assertEquals(GRANTEE, doc.getGranteeName());
        assertEquals(UpdateTypeDocument.CREATED, doc.getUpdateType());
        assertEquals(POA_ID, doc.getPaoId());
        assertEquals(ACCOUNT_NUMBER, doc.getAccountNumber());
        assertEquals(AuthorizationType.READ, doc.getNewAuthorization());
        assertEquals(AuthorizationType.WRITE, doc.getOldAuthorization());
        assertEquals(CREATED_AT, doc.getCreatedAt());
    }

    @Test
    void toDomain_shouldMapAllFields() {
        // Given
        val doc = givenAuditLogDocument();

        // When
        val auditLog = AuditLogMapper.toDomain(doc);

        // Then
        assertNotNull(auditLog);
        assertEquals(AUDIT_ID, auditLog.id());
        assertEquals(ACTOR, auditLog.actorName());
        assertEquals(GRANTEE, auditLog.granteeName());
        assertEquals(UpdateType.CREATED, auditLog.updateType());
        assertEquals(POA_ID, auditLog.paoId());
        assertEquals(ACCOUNT_NUMBER, auditLog.accountNumber());
        assertEquals(Authorization.READ, auditLog.newAuthorization());
        assertEquals(Authorization.WRITE, auditLog.oldAuthorization());
        assertEquals(CREATED_AT, auditLog.createdAt());
    }

    @Test
    void roundTrip_shouldPreserveAllValues() {
        // Given
        val original = givenAuditLog();

        // When
        val doc = AuditLogMapper.toDocument(original);
        val reconverted = AuditLogMapper.toDomain(doc);

        // Then
        assertEquals(original.id(), reconverted.id());
        assertEquals(original.actorName(), reconverted.actorName());
        assertEquals(original.granteeName(), reconverted.granteeName());
        assertEquals(original.updateType(), reconverted.updateType());
        assertEquals(original.paoId(), reconverted.paoId());
        assertEquals(original.accountNumber(), reconverted.accountNumber());
        assertEquals(original.newAuthorization(), reconverted.newAuthorization());
        assertEquals(original.oldAuthorization(), reconverted.oldAuthorization());
        assertEquals(original.createdAt(), reconverted.createdAt());
    }

    @Test
    void nullEnums_shouldMapToNull() {
        // Given: build a document with null enums
        val doc = givenAuditLogDocument().toBuilder()
                .updateType(null)
                .newAuthorization(null)
                .oldAuthorization(null)
                .build();

        // When
        val domain = AuditLogMapper.toDomain(doc);

        // Then
        assertNull(domain.updateType());
        assertNull(domain.newAuthorization());
        assertNull(domain.oldAuthorization());

        // When mapping from domain with nulls back to document
        val domainWithNulls = new nl.rabobank.audit.AuditLog(
                AUDIT_ID,
                ACTOR,
                GRANTEE,
                null,
                POA_ID,
                ACCOUNT_NUMBER,
                null,
                null,
                CREATED_AT
        );
        val mappedDoc = AuditLogMapper.toDocument(domainWithNulls);

        // Then
        assertNull(mappedDoc.getUpdateType());
        assertNull(mappedDoc.getNewAuthorization());
        assertNull(mappedDoc.getOldAuthorization());
    }
}
