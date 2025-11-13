package nl.rabobank.mongo.service;

import lombok.val;
import nl.rabobank.account.Account;
import nl.rabobank.account.PaymentAccount;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.mongo.documents.poa.AuthorizationType;
import nl.rabobank.mongo.documents.poa.PowerOfAttorneyDocument;
import nl.rabobank.mongo.repositories.AccountRepository;
import nl.rabobank.mongo.repositories.PowerOfAttorneyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static nl.rabobank.mongo.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PowerOfAttorneyServiceTest {


    @Mock
    private PowerOfAttorneyRepository powerOfAttorneyRepository;
    @Mock
    private AccountRepository accountRepository;
    @InjectMocks
    private PowerOfAttorneyService service;
    private Account account;

    @BeforeEach
    void setUp() {
        account = new PaymentAccount(ACCOUNT_NUMBER, GRANTOR, 1000.0);
    }

    @Test
    void save_shouldMapAndPersistAndReturnDomain() {
        // Given
        val input = givenPowerOfAttorney();
        val saved = givenPowerOfAttorneyDocument();

        when(powerOfAttorneyRepository.save(any(PowerOfAttorneyDocument.class))).thenReturn(saved);

        // When
        val result = service.save(input);

        // Then
        assertNotNull(result);
        assertEquals(GRANTOR, result.getGrantorName());
        assertEquals(GRANTEE, result.getGranteeName());
        assertEquals(account, result.getAccount());
        assertEquals(Authorization.READ, result.getAuthorization());

        val poaCaptor = ArgumentCaptor.forClass(PowerOfAttorneyDocument.class);
        verify(powerOfAttorneyRepository, times(1)).save(poaCaptor.capture());
        val toSave = poaCaptor.getValue();
        assertEquals(POA_ID, toSave.getId());
        assertEquals(GRANTOR, toSave.getGrantorName());
        assertEquals(GRANTEE, toSave.getGranteeName());
        assertEquals(ACCOUNT_NUMBER, toSave.getAccountNumber());
        assertEquals(AuthorizationType.READ, toSave.getAuthorizationType());
        assertFalse(toSave.isRevoked());
        assertNotNull(toSave.getCreatedAt());
    }

    @Test
    void findById_shouldReturnEmptyWhenPoaNotFound() {
        // Given
        when(powerOfAttorneyRepository.findById(POA_ID)).thenReturn(Optional.empty());

        // When
        val result = service.findById(POA_ID);

        // Then
        assertTrue(result.isEmpty());
        verify(powerOfAttorneyRepository, times(1)).findById(POA_ID);
        verifyNoInteractions(accountRepository);
    }

    @Test
    void findById_shouldReturnMappedDomainWhenFoundWithAccount() {
        // Given
        val poaDoc = givenPowerOfAttorneyDocument();
        when(powerOfAttorneyRepository.findById(POA_ID)).thenReturn(Optional.of(poaDoc));

        val accountDoc = givenPaymentAccountDocument();
        when(accountRepository.findById(ACCOUNT_NUMBER)).thenReturn(Optional.of(accountDoc));

        // When
        val result = service.findById(POA_ID);

        // Then
        assertTrue(result.isPresent());
        val poa = result.get();
        assertEquals(GRANTOR, poa.getGrantorName());
        assertEquals(GRANTEE, poa.getGranteeName());
        assertEquals(ACCOUNT_NUMBER, poa.getAccount().getAccountNumber());
        assertEquals(Authorization.READ, poa.getAuthorization());

        verify(powerOfAttorneyRepository, times(1)).findById(POA_ID);
        verify(accountRepository, times(1)).findById(ACCOUNT_NUMBER);
    }

    @Test
    void findById_shouldReturnEmptyWhenAccountMissing() {
        // Given
        val poaDoc = givenPowerOfAttorneyDocument();
        when(powerOfAttorneyRepository.findById(POA_ID)).thenReturn(Optional.of(poaDoc));
        when(accountRepository.findById(ACCOUNT_NUMBER)).thenReturn(Optional.empty());

        // When
        val result = service.findById(POA_ID);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void findByGranteeName_shouldJoinAccountsAndMapAll() {
        // Given
        val d1 = givenPowerOfAttorneyDocument();
        val d2 = givenPowerOfAttorneyDocument().toBuilder().id("id-2").accountNumber(OTHER_ACCOUNT_NUMBER).build();

        when(powerOfAttorneyRepository.findByGranteeNameAndRevokedFalse(GRANTEE))
                .thenReturn(List.of(d1, d2));

        val a1 = givenPaymentAccountDocument();
        val a2 = givenPaymentAccountDocument().toBuilder().accountNumber(OTHER_ACCOUNT_NUMBER).build();
        when(accountRepository.findAllByAccountNumberIn(List.of(ACCOUNT_NUMBER, OTHER_ACCOUNT_NUMBER)))
                .thenReturn(List.of(a1, a2));

        // When
        val result = service.findByGranteeName(GRANTEE);

        // Then
        assertEquals(2, result.size());
        assertEquals(ACCOUNT_NUMBER, result.get(0).getAccount().getAccountNumber());
        assertEquals(Authorization.READ, result.get(0).getAuthorization());
        assertEquals(OTHER_ACCOUNT_NUMBER, result.get(1).getAccount().getAccountNumber());
        assertEquals(Authorization.READ, result.get(1).getAuthorization());

        verify(powerOfAttorneyRepository, times(1)).findByGranteeNameAndRevokedFalse(GRANTEE);
        verify(accountRepository, times(1)).findAllByAccountNumberIn(List.of(ACCOUNT_NUMBER, OTHER_ACCOUNT_NUMBER));
    }

    @Test
    void findByGrantorName_shouldJoinAccountsAndMapAll() {
        // Given
        val d1 = givenPowerOfAttorneyDocument();
        when(powerOfAttorneyRepository.findByGrantorNameAndRevokedFalse(GRANTOR))
                .thenReturn(List.of(d1));

        val a1 = givenPaymentAccountDocument();
        when(accountRepository.findAllByAccountNumberIn(List.of(ACCOUNT_NUMBER)))
                .thenReturn(List.of(a1));

        // When
        val result = service.findByGrantorName(GRANTOR);

        // Then
        assertEquals(1, result.size());
        val poa = result.getFirst();
        assertEquals(GRANTOR, poa.getGrantorName());
        assertEquals(GRANTEE, poa.getGranteeName());
        assertEquals(ACCOUNT_NUMBER, poa.getAccount().getAccountNumber());
        assertEquals(Authorization.READ, poa.getAuthorization());

        verify(powerOfAttorneyRepository, times(1)).findByGrantorNameAndRevokedFalse(GRANTOR);
        verify(accountRepository, times(1)).findAllByAccountNumberIn(List.of(ACCOUNT_NUMBER));
    }

}
