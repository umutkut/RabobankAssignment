package nl.rabobank.service;

import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.exception.ForbiddenOperationException;
import nl.rabobank.exception.PowerOfAttorneyNotFoundException;
import nl.rabobank.repository.PowerOfAttorneyRepository;
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
    private PowerOfAttorneyRepository powerOfAttorneyRepository;

    @InjectMocks
    private DeletePowerOfAttorneyService service;

    @Test
    void deletes_whenGrantorMatches() {
        //Given
        when(powerOfAttorneyRepository.findById(POA_ID)).thenReturn(Optional.of(givenPowerOfAttorney()));

        //When
        service.deleteByIdAsGrantor(POA_ID, nl.rabobank.TestUtils.GRANTOR);

        //Then
        verify(powerOfAttorneyRepository).deleteById(POA_ID);
        verify(powerOfAttorneyRepository, times(1)).findById(POA_ID);
        verifyNoMoreInteractions(powerOfAttorneyRepository);
    }

    @Test
    void notFound_whenMissing() {
        when(powerOfAttorneyRepository.findById(POA_ID)).thenReturn(Optional.empty());

        //When and Then
        assertThrows(PowerOfAttorneyNotFoundException.class, () -> service.deleteByIdAsGrantor(POA_ID, nl.rabobank.TestUtils.GRANTOR));

        verify(powerOfAttorneyRepository, times(1)).findById(POA_ID);
        verify(powerOfAttorneyRepository, never()).deleteById(any());
        verifyNoMoreInteractions(powerOfAttorneyRepository);
    }

    @Test
    void forbidden_whenGrantorMismatch() {
        PowerOfAttorney poa = givenPowerOfAttorney().toBuilder().grantorName("other-grantor").build();

        when(powerOfAttorneyRepository.findById(POA_ID)).thenReturn(Optional.of(poa));

        //When and Then
        assertThrows(ForbiddenOperationException.class, () -> service.deleteByIdAsGrantor(POA_ID, nl.rabobank.TestUtils.GRANTOR));

        verify(powerOfAttorneyRepository, times(1)).findById(POA_ID);
        verify(powerOfAttorneyRepository, never()).deleteById(any());
        verifyNoMoreInteractions(powerOfAttorneyRepository);
    }
}
