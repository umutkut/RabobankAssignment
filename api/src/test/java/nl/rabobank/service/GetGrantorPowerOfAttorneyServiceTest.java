package nl.rabobank.service;

import lombok.val;
import nl.rabobank.repository.PowerOfAttorneyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static nl.rabobank.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetGrantorPowerOfAttorneyServiceTest {

    @Mock
    PowerOfAttorneyRepository powerOfAttorneyRepository;

    @InjectMocks
    GetGrantorPowerOfAttorneyService service;

    @Test
    void listPoasForGrantor() {
        // Given
        val poa1 = givenPowerOfAttorney();
        val poa2 = givenPowerOfAttorney().toBuilder().id("poa-2").account(givenSavingsAccount()).build();
        when(powerOfAttorneyRepository.findByGrantorName(eq(GRANTOR)))
                .thenReturn(List.of(poa1, poa2));

        // When
        val result = service.listPoasForGrantor(GRANTOR);

        // Then
        assertThat(result).containsExactly(poa1, poa2);
    }
}
