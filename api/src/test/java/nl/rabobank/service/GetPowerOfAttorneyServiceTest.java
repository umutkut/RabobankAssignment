package nl.rabobank.service;

import lombok.val;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.exception.PowerOfAttorneyNotFoundException;
import nl.rabobank.repository.PowerOfAttorneyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static nl.rabobank.controller.TestUtils.POA_ID;
import static nl.rabobank.controller.TestUtils.givenPowerOfAttorney;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPowerOfAttorneyServiceTest {

    @Mock
    private PowerOfAttorneyRepository powerOfAttorneyRepository;

    @InjectMocks
    private GetPowerOfAttorneyService service;

    @Test
    void getById_shouldReturnPoa_whenExists() {
        // given
        val expected = givenPowerOfAttorney();
        when(powerOfAttorneyRepository.findById(POA_ID)).thenReturn(Optional.of(expected));

        // when
        PowerOfAttorney poa = service.getById(POA_ID);

        // then
        assertNotNull(poa);
        assertEquals(expected, poa);
        verify(powerOfAttorneyRepository, times(1)).findById(POA_ID);
        verifyNoMoreInteractions(powerOfAttorneyRepository);
    }

    @Test
    void getById_shouldThrowNotFound_whenMissing() {
        // given
        when(powerOfAttorneyRepository.findById(POA_ID)).thenReturn(Optional.empty());

        // when and then
        assertThrows(PowerOfAttorneyNotFoundException.class, () -> service.getById(POA_ID));
        verify(powerOfAttorneyRepository, times(1)).findById(POA_ID);
        verifyNoMoreInteractions(powerOfAttorneyRepository);
    }
}
