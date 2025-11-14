package nl.rabobank.service;

import lombok.val;
import nl.rabobank.repository.PowerOfAttorneyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static nl.rabobank.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
        val pageable = PageRequest.of(0, 2);
        val page = new PageImpl<>(List.of(poa1, poa2), pageable, 5);

        when(powerOfAttorneyRepository.findByGrantorName(eq(GRANTOR), any(Pageable.class)))
                .thenReturn(page);

        // When
        val result = service.listPoasForGrantor(GRANTOR, pageable);

        // Then
        assertThat(result.getContent()).containsExactly(poa1, poa2);
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getNumber()).isZero();
    }
}
