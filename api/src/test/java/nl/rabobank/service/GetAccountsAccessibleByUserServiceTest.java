package nl.rabobank.service;

import lombok.val;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.repository.AccountService;
import nl.rabobank.repository.PowerOfAttorneyService;
import nl.rabobank.service.model.AccountWithAuthorization;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static nl.rabobank.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAccountsAccessibleByUserServiceTest {

    @Mock
    PowerOfAttorneyService powerOfAttorneyService;

    @Mock
    AccountService accountService;

    @InjectMocks
    GetAccountsAccessibleByUserService service;

    @Test
    void listAccountsAccessibleByUser() {
        //Given
        val ownAccount = givenSavingsAccount();
        val ownAccount2 = givenPaymentAccount().toBuilder().accountNumber("NL91RABO3234567890").build();
        when(accountService.findAllByAccountHolderName(GRANTEE)).thenReturn(List.of(ownAccount, ownAccount2));

        val poa1 = givenPowerOfAttorney();
        when(powerOfAttorneyService.findByGranteeName(GRANTEE))
                .thenReturn(List.of(poa1));

        //When
        val result = service.listAccountsAccessibleByUser(GRANTEE);

        //Then
        assertThat(result).extracting(aa -> aa.account().accountNumber())
                .containsExactly(ACCOUNT_NUMBER, OTHER_ACCOUNT_NUMBER, "NL91RABO3234567890");
        assertThat(result).extracting(AccountWithAuthorization::authorization)
                .containsExactly(Authorization.READ, Authorization.WRITE, Authorization.WRITE);
    }
}
