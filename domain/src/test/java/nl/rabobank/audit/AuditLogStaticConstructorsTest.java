package nl.rabobank.audit;

import lombok.val;
import nl.rabobank.account.PaymentAccount;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class AuditLogStaticConstructorsTest {

    private static final String ID = "audit-123";
    private static final String POA_ID = "poa-1";
    private static final String ACCOUNT_NUMBER = "NL91RABO1234567890";
    private static final String GRANTOR = "Alice";
    private static final String GRANTEE = "Bob";
    private static final Instant CREATED_AT = Instant.parse("2024-01-01T00:00:00Z");

    private PowerOfAttorney givenPoa(Authorization authorization) {
        val account = new PaymentAccount(ACCOUNT_NUMBER, GRANTOR, 1000.0);
        return PowerOfAttorney.builder()
                .id(POA_ID)
                .granteeName(GRANTEE)
                .grantorName(GRANTOR)
                .account(account)
                .authorization(authorization)
                .createdAt(CREATED_AT)
                .updatedAt(CREATED_AT)
                .build();
    }

    @Test
    void created_shouldPopulateAllFieldsFromPoa() {
        // Given
        val poa = givenPoa(Authorization.READ);

        // When
        val log = AuditLog.created(ID, CREATED_AT, poa);

        // Then
        assertNotNull(log);
        assertEquals(ID, log.id());
        assertEquals(GRANTOR, log.actorName());
        assertEquals(GRANTEE, log.granteeName());
        assertEquals(UpdateType.CREATED, log.updateType());
        assertEquals(POA_ID, log.poaId());
        assertEquals(ACCOUNT_NUMBER, log.accountNumber());
        assertEquals(Authorization.READ, log.newAuthorization());
        assertNull(log.oldAuthorization());
        assertEquals(CREATED_AT, log.createdAt());
    }

    @Test
    void updated_shouldPopulateAllFieldsFromNewPoaAndOldAuthorization() {
        // Given
        val newPoa = givenPoa(Authorization.WRITE);
        val oldAuth = Authorization.READ;

        // When
        val log = AuditLog.updated(ID, CREATED_AT, oldAuth, newPoa);

        // Then
        assertNotNull(log);
        assertEquals(ID, log.id());
        assertEquals(GRANTOR, log.actorName());
        assertEquals(GRANTEE, log.granteeName());
        assertEquals(UpdateType.UPDATED, log.updateType());
        assertEquals(POA_ID, log.poaId());
        assertEquals(ACCOUNT_NUMBER, log.accountNumber());
        assertEquals(Authorization.WRITE, log.newAuthorization());
        assertEquals(Authorization.READ, log.oldAuthorization());
        assertEquals(CREATED_AT, log.createdAt());
    }

    @Test
    void deleted_shouldPopulateAllFieldsFromPoa() {
        // Given
        val poa = givenPoa(Authorization.READ);

        // When
        val log = AuditLog.deleted(ID, CREATED_AT, poa);

        // Then
        assertNotNull(log);
        assertEquals(ID, log.id());
        assertEquals(GRANTOR, log.actorName());
        assertEquals(GRANTEE, log.granteeName());
        assertEquals(UpdateType.DELETED, log.updateType());
        assertEquals(POA_ID, log.poaId());
        assertEquals(ACCOUNT_NUMBER, log.accountNumber());
        assertNull(log.newAuthorization());
        assertEquals(Authorization.READ, log.oldAuthorization());
        assertEquals(CREATED_AT, log.createdAt());
    }
}
