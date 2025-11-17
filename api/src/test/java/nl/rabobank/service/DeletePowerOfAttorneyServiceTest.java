package nl.rabobank.service;

import nl.rabobank.audit.AuditEventsPublisher;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.exception.ForbiddenOperationException;
import nl.rabobank.exception.PowerOfAttorneyNotFoundException;
import nl.rabobank.repository.PowerOfAttorneyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static nl.rabobank.TestUtils.POA_ID;
import static nl.rabobank.TestUtils.givenPowerOfAttorney;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeletePowerOfAttorneyServiceTest {

    @Mock
    private PowerOfAttorneyService powerOfAttorneyService;
    @Mock
    private AuditEventsPublisher auditEventsPublisher;

    @InjectMocks
    private DeletePowerOfAttorneyService service;

    @Test
    void deletes_whenGrantorMatches() {
        //Given
        PowerOfAttorney poa = givenPowerOfAttorney();
        when(powerOfAttorneyService.findById(POA_ID)).thenReturn(Optional.of(poa));

        //When
        service.deleteByIdAsGrantor(POA_ID, nl.rabobank.TestUtils.GRANTOR);

        //Then
        verify(powerOfAttorneyService).deleteById(POA_ID);
        verify(powerOfAttorneyService, times(1)).findById(POA_ID);
        verify(auditEventsPublisher, times(1)).publishDeleted(poa);
        verifyNoMoreInteractions(powerOfAttorneyService, auditEventsPublisher);
    }

    @Test
    void notFound_whenMissing() {
        when(powerOfAttorneyService.findById(POA_ID)).thenReturn(Optional.empty());

        //When and Then
        assertThrows(PowerOfAttorneyNotFoundException.class, () -> service.deleteByIdAsGrantor(POA_ID, nl.rabobank.TestUtils.GRANTOR));

        verify(powerOfAttorneyService, times(1)).findById(POA_ID);
        verify(powerOfAttorneyService, never()).deleteById(any());
        verifyNoInteractions(auditEventsPublisher);
        verifyNoMoreInteractions(powerOfAttorneyService);
    }

    @Test
    void forbidden_whenGrantorMismatch() {
        PowerOfAttorney poa = givenPowerOfAttorney().toBuilder().grantorName("other-grantor").build();

        when(powerOfAttorneyService.findById(POA_ID)).thenReturn(Optional.of(poa));

        //When and Then
        assertThrows(ForbiddenOperationException.class, () -> service.deleteByIdAsGrantor(POA_ID, nl.rabobank.TestUtils.GRANTOR));

        verify(powerOfAttorneyService, times(1)).findById(POA_ID);
        verify(powerOfAttorneyService, never()).deleteById(any());
        verifyNoInteractions(auditEventsPublisher);
        verifyNoMoreInteractions(powerOfAttorneyService);
    }
}
