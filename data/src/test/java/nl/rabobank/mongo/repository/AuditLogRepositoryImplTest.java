package nl.rabobank.mongo.repository;

import lombok.val;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.mongo.client.AuditLogMongoClient;
import nl.rabobank.mongo.documents.audit.AuditLogDocument;
import nl.rabobank.mongo.documents.poa.AuthorizationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static nl.rabobank.mongo.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogRepositoryImplTest {

    @Mock
    private AuditLogMongoClient client;

    @InjectMocks
    private AuditLogRepositoryImpl repository;

    @Test
    void save_shouldMapAndPersistAndReturnDomain() {
        // Given
        val toSave = givenAuditLog();
        val savedDoc = givenAuditLogDocument();
        when(client.save(any(AuditLogDocument.class))).thenReturn(savedDoc);

        // When
        val result = repository.save(toSave);

        // Then mapped back to domain
        assertNotNull(result);
        assertEquals(AUDIT_ID, result.id());
        assertEquals(ACTOR, result.actorName());
        assertEquals(GRANTEE, result.granteeName());
        assertEquals(Authorization.READ, result.newAuthorization());
        assertEquals(Authorization.WRITE, result.oldAuthorization());
        assertEquals(CREATED_AT, result.createdAt());

        // Verify mapping used for saving
        val captor = ArgumentCaptor.forClass(AuditLogDocument.class);
        verify(client, times(1)).save(captor.capture());
        val docArg = captor.getValue();
        assertEquals(AUDIT_ID, docArg.getId());
        assertEquals(ACTOR, docArg.getActorName());
        assertEquals(GRANTEE, docArg.getGranteeName());
        assertEquals(POA_ID, docArg.getPaoId());
        assertEquals(ACCOUNT_NUMBER, docArg.getAccountNumber());
        assertEquals(AuthorizationType.READ, docArg.getNewAuthorization());
        assertEquals(AuthorizationType.WRITE, docArg.getOldAuthorization());
        assertEquals(CREATED_AT, docArg.getCreatedAt());
    }

    @Test
    void findByAccountNumber_shouldMapPage() {
        // Given
        val pageable = PageRequest.of(0, 10);
        val d1 = givenAuditLogDocument();
        val d2 = givenAuditLogDocument().toBuilder()
                .id("audit-2")
                .actorName("another-user")
                .build();
        val page = new PageImpl<>(List.of(d1, d2), pageable, 2);
        when(client.findByAccountNumber(ACCOUNT_NUMBER, pageable)).thenReturn(page);

        // When
        val resultPage = repository.findByAccountNumber(ACCOUNT_NUMBER, pageable);

        // Then
        assertEquals(2, resultPage.getTotalElements());
        val content = resultPage.getContent();
        assertEquals(2, content.size());
        assertEquals(AUDIT_ID, content.get(0).id());
        assertEquals("another-user", content.get(1).actorName());
        assertEquals(Authorization.READ, content.get(0).newAuthorization());
        assertEquals(Authorization.WRITE, content.get(0).oldAuthorization());

        verify(client, times(1)).findByAccountNumber(ACCOUNT_NUMBER, pageable);
    }

    @Test
    void findByActorName_shouldMapPage() {
        // Given
        val pageable = PageRequest.of(0, 5);
        val d1 = givenAuditLogDocument();
        val page = new PageImpl<>(List.of(d1), pageable, 1);
        when(client.findByActorName(ACTOR, pageable)).thenReturn(page);

        // When
        val resultPage = repository.findByActorName(ACTOR, pageable);

        // Then
        assertEquals(1, resultPage.getTotalElements());
        val item = resultPage.getContent().getFirst();
        assertEquals(ACTOR, item.actorName());
        assertEquals(Authorization.READ, item.newAuthorization());
        assertEquals(Authorization.WRITE, item.oldAuthorization());
        verify(client, times(1)).findByActorName(ACTOR, pageable);
    }
}
