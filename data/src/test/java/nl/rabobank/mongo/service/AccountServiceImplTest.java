package nl.rabobank.mongo.service;

import lombok.val;
import nl.rabobank.account.Account;
import nl.rabobank.account.PaymentAccount;
import nl.rabobank.account.SavingsAccount;
import nl.rabobank.mongo.repository.AccountMongoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static nl.rabobank.mongo.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountMongoRepository accountMongoRepository;

    @InjectMocks
    private AccountServiceImpl service;

    @Test
    void findByAccountNumber_shouldReturnEmptyWhenNotFound() {
        when(accountMongoRepository.findById(ACCOUNT_NUMBER)).thenReturn(Optional.empty());

        assertTrue(service.findByAccountNumber(ACCOUNT_NUMBER).isEmpty());
        verify(accountMongoRepository, times(1)).findById(ACCOUNT_NUMBER);
        verifyNoMoreInteractions(accountMongoRepository);
    }

    @Test
    void findByAccountNumber_shouldMapPaymentAccountDocumentToDomain() {
        val paymentAccountDocument = givenPaymentAccountDocument();
        when(accountMongoRepository.findById(ACCOUNT_NUMBER)).thenReturn(Optional.of(paymentAccountDocument));

        Optional<Account> result = service.findByAccountNumber(ACCOUNT_NUMBER);

        assertTrue(result.isPresent());
        assertInstanceOf(PaymentAccount.class, result.get());
        PaymentAccount acc = (PaymentAccount) result.get();
        assertEquals(ACCOUNT_NUMBER, acc.accountNumber());
        assertEquals(GRANTOR, acc.accountHolderName());
        assertEquals(BALANCE, acc.balance());
        verify(accountMongoRepository, times(1)).findById(ACCOUNT_NUMBER);
    }

    @Test
    void findByAccountNumber_shouldMapSavingsAccountDocumentToDomain() {
        val savingsAccountDocument = givenSavingsAccountDocument();
        when(accountMongoRepository.findById(ACCOUNT_NUMBER)).thenReturn(Optional.of(savingsAccountDocument));

        Optional<Account> result = service.findByAccountNumber(ACCOUNT_NUMBER);

        assertTrue(result.isPresent());
        assertInstanceOf(SavingsAccount.class, result.get());
        val acc = (SavingsAccount) result.get();
        assertEquals(ACCOUNT_NUMBER, acc.accountNumber());
        assertEquals(GRANTOR, acc.accountHolderName());
        assertEquals(BALANCE, acc.balance());
        verify(accountMongoRepository, times(1)).findById(ACCOUNT_NUMBER);
    }
}
