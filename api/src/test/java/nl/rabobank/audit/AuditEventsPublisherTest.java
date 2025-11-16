package nl.rabobank.audit;

import lombok.val;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.service.IdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Clock;

import static nl.rabobank.TestUtils.CREATED_AT;
import static nl.rabobank.TestUtils.givenPowerOfAttorney;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditEventsPublisherTest {

    @Mock
    private ApplicationEventPublisher springEvents;
    @Mock
    private IdGenerator idGenerator;
    @Mock
    private Clock clock;

    @InjectMocks
    private AuditEventsPublisher publisher;

    @Test
    void publishCreated() {
        // Given
        val poa = givenPowerOfAttorney();
        when(idGenerator.generateUUID()).thenReturn("audit-id");
        when(clock.instant()).thenReturn(CREATED_AT);

        // When
        publisher.publishCreated(poa);

        // Then
        val captor = ArgumentCaptor.forClass(AuditLogEvent.class);
        verify(springEvents, times(1)).publishEvent(captor.capture());
        val event = captor.getValue();
        assertEquals(AuditLog.created("audit-id", CREATED_AT, poa), event.auditLog());
    }

    @Test
    void publishUpdated() {
        // Given
        val poa = givenPowerOfAttorney().toBuilder().authorization(Authorization.WRITE).build();
        when(idGenerator.generateUUID()).thenReturn("audit-id-2");
        when(clock.instant()).thenReturn(CREATED_AT);

        // When
        publisher.publishUpdated(Authorization.READ, poa);

        // Then
        val captor = ArgumentCaptor.forClass(AuditLogEvent.class);
        verify(springEvents, times(1)).publishEvent(captor.capture());
        val event = captor.getValue();
        assertEquals(AuditLog.updated("audit-id-2", CREATED_AT, Authorization.READ, poa), event.auditLog());
    }

    @Test
    void publishDeleted() {
        // Given
        val poa = givenPowerOfAttorney();
        when(idGenerator.generateUUID()).thenReturn("audit-id-3");
        when(clock.instant()).thenReturn(CREATED_AT);

        // When
        publisher.publishDeleted(poa);

        // Then
        val captor = ArgumentCaptor.forClass(AuditLogEvent.class);
        verify(springEvents, times(1)).publishEvent(captor.capture());
        val event = captor.getValue();
        assertEquals(AuditLog.deleted("audit-id-3", CREATED_AT, poa), event.auditLog());
    }
}
