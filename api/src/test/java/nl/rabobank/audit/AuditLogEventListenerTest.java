package nl.rabobank.audit;

import lombok.val;
import nl.rabobank.repository.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static nl.rabobank.TestUtils.givenPowerOfAttorney;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuditLogEventListenerTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogEventListener listener;

    @Test
    void listenAuditLogEvent() {
        // Given
        val poa = givenPowerOfAttorney();
        val log = AuditLog.created("audit-id", Instant.now(), poa);

        val event = new AuditLogEvent(log);

        // When
        listener.listenAuditLogEvent(event);

        // Then
        verify(auditLogRepository, times(1)).save(log);
    }
}
