package nl.rabobank.service;

import lombok.val;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.exception.PowerOfAttorneyNotFoundException;
import nl.rabobank.repository.PowerOfAttorneyRepository;
import nl.rabobank.service.model.UpdatePowerOfAttorneyAuthorizationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.util.Optional;

import static nl.rabobank.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdatePowerOfAttorneyAuthorizationServiceTest {

    @Mock
    private PowerOfAttorneyRepository powerOfAttorneyRepository;
    @Mock
    private Clock clock;

    @InjectMocks
    private UpdatePowerOfAttorneyAuthorizationService service;

    @Test
    void updateAuthorization_shouldUpdateAndPersist_whenPoaExists() {
        // given
        val existing = givenPowerOfAttorney(); // has Authorization.READ, CREATED_AT, UPDATED_AT
        when(powerOfAttorneyRepository.findById(POA_ID)).thenReturn(Optional.of(existing));
        when(clock.instant()).thenReturn(UPDATED_AT);
        when(powerOfAttorneyRepository.save(any(PowerOfAttorney.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        val request = new UpdatePowerOfAttorneyAuthorizationRequest(POA_ID, Authorization.WRITE);

        // when
        val result = service.updateAuthorization(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(existing.id());
        assertThat(result.authorization()).isEqualTo(Authorization.WRITE);
        assertThat(result.updatedAt()).isEqualTo(UPDATED_AT);
        assertThat(result.grantorName()).isEqualTo(existing.grantorName());
        assertThat(result.granteeName()).isEqualTo(existing.granteeName());
        assertThat(result.account()).isEqualTo(existing.account());
        assertThat(result.createdAt()).isEqualTo(existing.createdAt());

        ArgumentCaptor<PowerOfAttorney> captor = ArgumentCaptor.forClass(PowerOfAttorney.class);
        verify(powerOfAttorneyRepository).findById(POA_ID);
        verify(powerOfAttorneyRepository).save(captor.capture());
        verifyNoMoreInteractions(powerOfAttorneyRepository);

        val saved = captor.getValue();
        assertThat(saved.authorization()).isEqualTo(Authorization.WRITE);
        assertThat(saved.updatedAt()).isEqualTo(UPDATED_AT);
    }

    @Test
    void updateAuthorization_shouldThrowNotFound_whenPoaMissing() {
        // given
        when(powerOfAttorneyRepository.findById(POA_ID)).thenReturn(Optional.empty());

        val request = new UpdatePowerOfAttorneyAuthorizationRequest(POA_ID, Authorization.READ);

        // when / then
        assertThrows(PowerOfAttorneyNotFoundException.class, () -> service.updateAuthorization(request));
        verify(powerOfAttorneyRepository, times(1)).findById(POA_ID);
        verify(powerOfAttorneyRepository, never()).save(any());
        verifyNoMoreInteractions(powerOfAttorneyRepository);
        verifyNoInteractions(clock);
    }

    @Test
    void updateAuthorization_shouldReturnExistingWithoutPersist_whenAuthorizationUnchanged() {
        // given
        val existing = givenPowerOfAttorney(); // Authorization.READ
        when(powerOfAttorneyRepository.findById(POA_ID)).thenReturn(Optional.of(existing));
        val request = new UpdatePowerOfAttorneyAuthorizationRequest(POA_ID, Authorization.READ);

        // when
        val result = service.updateAuthorization(request);

        // then
        assertThat(result).isSameAs(existing);
        verify(powerOfAttorneyRepository, times(1)).findById(POA_ID);
        verify(powerOfAttorneyRepository, never()).save(any());
        verifyNoMoreInteractions(powerOfAttorneyRepository);
        verifyNoInteractions(clock);
    }
}
