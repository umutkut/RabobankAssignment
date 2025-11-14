package nl.rabobank.service;

import lombok.val;
import nl.rabobank.account.PaymentAccount;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.exception.AccountNotFoundException;
import nl.rabobank.exception.PowerOfAttorneyAlreadyExistException;
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

import static nl.rabobank.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatePowerOfAttorneyServiceTest {

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
        when(powerOfAttorneyRepository.findByGrantorAndGranteeAndAccountNumber(GRANTOR, GRANTEE, ACCOUNT_NUMBER))
                .thenReturn(Optional.empty());
        when(idGenerator.generateUUID()).thenReturn(POA_ID);
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
        assertEquals(POA_ID, result.id());
        assertEquals(GRANTEE, result.granteeName());
        assertEquals(GRANTOR, result.grantorName());
        assertEquals(account, result.account());
        assertEquals(Authorization.WRITE, result.authorization());

        verify(accountRepository, times(1)).findByAccountNumber(ACCOUNT_NUMBER);
        verify(powerOfAttorneyRepository, times(1)).findByGrantorAndGranteeAndAccountNumber(GRANTOR, GRANTEE, ACCOUNT_NUMBER);
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
                GRANTOR,
                GRANTEE,
                ACCOUNT_NUMBER,
                Authorization.READ
        );

        // when / then
        assertThrows(UnsupportedUserOperationException.class, () -> service.create(request));
        verify(accountRepository, times(1)).findByAccountNumber(ACCOUNT_NUMBER);
        verifyNoInteractions(powerOfAttorneyRepository, idGenerator);
    }

    @Test
    void create_shouldThrowConflict_whenPoaAlreadyExists() {
        // given
        val account = new PaymentAccount(ACCOUNT_NUMBER, GRANTOR, BALANCE);
        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(account));
        when(powerOfAttorneyRepository.findByGrantorAndGranteeAndAccountNumber(GRANTOR, GRANTEE, ACCOUNT_NUMBER))
                .thenReturn(Optional.of(PowerOfAttorney.builder().id(POA_ID).account(account).grantorName(GRANTOR).granteeName(GRANTEE).authorization(Authorization.READ).build()));

        val request = new CreatePowerOfAttorneyServiceRequest(
                GRANTOR,
                GRANTEE,
                ACCOUNT_NUMBER,
                Authorization.READ
        );

        // when / then
        assertThrows(PowerOfAttorneyAlreadyExistException.class, () -> service.create(request));
        verify(accountRepository, times(1)).findByAccountNumber(ACCOUNT_NUMBER);
        verify(powerOfAttorneyRepository, times(1)).findByGrantorAndGranteeAndAccountNumber(GRANTOR, GRANTEE, ACCOUNT_NUMBER);
        verifyNoInteractions(idGenerator);
        verify(powerOfAttorneyRepository, never()).save(any());
    }
}
