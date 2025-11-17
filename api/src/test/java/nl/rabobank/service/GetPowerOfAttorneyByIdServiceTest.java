package nl.rabobank.service;

import lombok.val;
import nl.rabobank.authorizations.PowerOfAttorney;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPowerOfAttorneyByIdServiceTest {

    @Mock
    private PowerOfAttorneyService powerOfAttorneyService;

    @InjectMocks
    private GetPowerOfAttorneyByIdService service;

    @Test
    void getById_shouldReturnPoa_whenExists() {
        // given
        val expected = givenPowerOfAttorney();
        when(powerOfAttorneyService.findById(POA_ID)).thenReturn(Optional.of(expected));

        // when
        PowerOfAttorney poa = service.getById(POA_ID);

        // then
        assertNotNull(poa);
        assertEquals(expected, poa);
        verify(powerOfAttorneyService, times(1)).findById(POA_ID);
        verifyNoMoreInteractions(powerOfAttorneyService);
    }

    @Test
    void getById_shouldThrowNotFound_whenMissing() {
        // given
        when(powerOfAttorneyService.findById(POA_ID)).thenReturn(Optional.empty());

        // when and then
        assertThrows(PowerOfAttorneyNotFoundException.class, () -> service.getById(POA_ID));
        verify(powerOfAttorneyService, times(1)).findById(POA_ID);
        verifyNoMoreInteractions(powerOfAttorneyService);
    }
}
