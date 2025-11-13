package nl.rabobank.service;

import lombok.val;
import nl.rabobank.account.PaymentAccount;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.exception.AccountNotFoundException;
import nl.rabobank.exception.UnsupportedUserOperationException;
import nl.rabobank.repository.AccountRepository;
import nl.rabobank.repository.PowerOfAttorneyRepository;
import nl.rabobank.service.model.CreatePowerOfAttorneyServiceRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatePowerOfAttorneyServiceTest {

    private static final double BALANCE = 1000.0;
    private static final String GENERATED_ID = "uuid-123";
    private static final String ACCOUNT_NUMBER = "NL13RABO0987654321";
    private static final String GRANTOR = "Cheddar Dincer Tanis";
    private static final String GRANTEE = "Merkur Dincer Tanis";

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private PowerOfAttorneyRepository powerOfAttorneyRepository;
    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private CreatePowerOfAttorneyService service;

    @Test
    void create_shouldPersistAndReturnPoa_whenRequestIsValid() {
        // given
        val account = new PaymentAccount(ACCOUNT_NUMBER, GRANTOR, BALANCE);
        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(account));
        when(idGenerator.generateUUID()).thenReturn(GENERATED_ID);
        when(powerOfAttorneyRepository.save(any(PowerOfAttorney.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        val request = new CreatePowerOfAttorneyServiceRequest(
                GRANTOR,
                GRANTEE,
                ACCOUNT_NUMBER,
                Authorization.WRITE
        );

        // when
        val result = service.create(request);

        // then
        assertNotNull(result);
        assertEquals(GENERATED_ID, result.getId());
        assertEquals(GRANTEE, result.getGranteeName());
        assertEquals(GRANTOR, result.getGrantorName());
        assertEquals(account, result.getAccount());
        assertEquals(Authorization.WRITE, result.getAuthorization());

        verify(accountRepository, times(1)).findByAccountNumber(ACCOUNT_NUMBER);
        verify(idGenerator, times(1)).generateUUID();
        verify(powerOfAttorneyRepository, times(1)).save(any(PowerOfAttorney.class));
        verifyNoMoreInteractions(accountRepository, idGenerator, powerOfAttorneyRepository);
    }

    @Test
    void create_shouldThrowAccountNotFound_whenAccountMissing() {
        // given
        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.empty());

        val request = new CreatePowerOfAttorneyServiceRequest(
                GRANTOR,
                GRANTEE,
                ACCOUNT_NUMBER,
                Authorization.READ
        );

        // when / then
        assertThrows(AccountNotFoundException.class, () -> service.create(request));
        verify(accountRepository, times(1)).findByAccountNumber(ACCOUNT_NUMBER);
        verifyNoInteractions(powerOfAttorneyRepository, idGenerator);
    }

    @Test
    void create_shouldThrowUnsupportedUserOperation_whenGrantorDoesNotOwnAccount() {
        // given
        val account = new PaymentAccount(ACCOUNT_NUMBER, "Different Owner", BALANCE);
        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(account));

        val request = new CreatePowerOfAttorneyServiceRequest(
                GRANTOR, // request grantor does not match account holder name
                GRANTEE,
                ACCOUNT_NUMBER,
                Authorization.READ
        );

        // when / then
        assertThrows(UnsupportedUserOperationException.class, () -> service.create(request));
        verify(accountRepository, times(1)).findByAccountNumber(ACCOUNT_NUMBER);
        verifyNoInteractions(powerOfAttorneyRepository, idGenerator);
    }
}
