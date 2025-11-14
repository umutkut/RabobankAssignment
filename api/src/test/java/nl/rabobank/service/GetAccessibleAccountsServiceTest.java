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
class GetAccessibleAccountsServiceTest {

    @Mock
    PowerOfAttorneyRepository powerOfAttorneyRepository;

    @InjectMocks
    GetAccessibleAccountsService service;

    @Test
    void listPoasForUser_appliesPaginationAndOrder() {
        val poa1 = givenPowerOfAttorney();
        val poa2 = givenPowerOfAttorney().toBuilder().id("poa-2").account(givenSavingsAccount()).build();
        val pageable = PageRequest.of(0, 2);
        val page = new PageImpl<>(List.of(poa1, poa2), pageable, 3);

        when(powerOfAttorneyRepository.findByGranteeName(eq(GRANTEE), any(Pageable.class)))
                .thenReturn(page);

        val result = service.listPoasForUser(GRANTEE, pageable);

        assertThat(result.getContent()).containsExactly(poa1, poa2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getNumber()).isZero();
    }
}
