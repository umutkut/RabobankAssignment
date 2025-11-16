package nl.rabobank.service;

import lombok.val;
import nl.rabobank.audit.AuditLog;
import nl.rabobank.repository.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static nl.rabobank.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetActorAuditLogsServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private GetActorAuditLogsService service;

    @Test
    void listByActor() {
        // Given
        val pageable = PageRequest.of(0, 2);
        val poa = givenPowerOfAttorney();
        val log = AuditLog.created("audit-id", CREATED_AT, poa);

        val expected = new PageImpl<>(List.of(log), pageable, 1);

        when(auditLogRepository.findByActorName(GRANTOR, pageable)).thenReturn(expected);

        // When
        val result = service.listByActor(GRANTOR, pageable);

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals("audit-id", result.getContent().getFirst().id());

        verify(auditLogRepository, times(1)).findByActorName(GRANTOR, pageable);
        verifyNoMoreInteractions(auditLogRepository);
    }
}
