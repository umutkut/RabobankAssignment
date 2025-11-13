package nl.rabobank.mongo.mapper;

import lombok.val;
import nl.rabobank.account.Account;
import nl.rabobank.account.PaymentAccount;
import nl.rabobank.account.SavingsAccount;
import nl.rabobank.mongo.documents.account.AccountType;
import nl.rabobank.mongo.documents.account.PaymentAccountDocument;
import nl.rabobank.mongo.documents.account.SavingsAccountDocument;
import org.junit.jupiter.api.Test;

import static nl.rabobank.mongo.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class AccountMapperTest {


    @Test
    void shouldNotInstantiateUtilityClass() {
        // When/Then
        assertThrows(IllegalStateException.class, AccountMapper::new);
    }

    @Test
    void shouldConvertPaymentAccountToDocument() {
        // Given
        val paymentAccount = givenPaymentAccount();

        // When
        val document = AccountMapper.toDocument(paymentAccount);

        // Then
        assertNotNull(document);
        assertInstanceOf(PaymentAccountDocument.class, document);
        assertEquals(ACCOUNT_NUMBER, document.getAccountNumber());
        assertEquals(GRANTOR, document.getAccountHolderName());
        assertEquals(BALANCE, document.getBalance());
        assertEquals(AccountType.PAYMENT, document.getAccountType());
    }

    @Test
    void shouldConvertSavingsAccountToDocument() {
        // Given
        val savingsAccount = givenSavingsAccount();

        // When
        val document = AccountMapper.toDocument(savingsAccount);

        // Then
        assertNotNull(document);
        assertInstanceOf(SavingsAccountDocument.class, document);
        assertEquals(ACCOUNT_NUMBER, document.getAccountNumber());
        assertEquals(GRANTOR, document.getAccountHolderName());
        assertEquals(BALANCE, document.getBalance());
        assertEquals(AccountType.SAVINGS, document.getAccountType());
    }

    @Test
    void shouldThrowExceptionForUnknownAccountType() {
        // Given
        val unknownAccount = new Account() {
            @Override
            public String getAccountNumber() {
                return ACCOUNT_NUMBER;
            }

            @Override
            public String getAccountHolderName() {
                return GRANTOR;
            }

            @Override
            public Double getBalance() {
                return BALANCE;
            }
        };

        // When/Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> AccountMapper.toDocument(unknownAccount)
        );
        assertEquals("Unknown account type", exception.getMessage());
    }

    @Test
    void shouldConvertPaymentAccountDocumentToDomain() {
        // Given
        val document = PaymentAccountDocument.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .accountHolderName(GRANTOR)
                .balance(1000.0)
                .build();

        // When
        val account = AccountMapper.toDomain(document);

        // Then
        assertNotNull(account);
        assertInstanceOf(PaymentAccount.class, account);
        assertEquals(ACCOUNT_NUMBER, account.getAccountNumber());
        assertEquals(GRANTOR, account.getAccountHolderName());
        assertEquals(1000.0, account.getBalance());
    }

    @Test
    void shouldConvertSavingsAccountDocumentToDomain() {
        // Given
        val document = SavingsAccountDocument.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .accountHolderName(GRANTOR)
                .balance(5000.0)
                .build();

        // When
        val account = AccountMapper.toDomain(document);

        // Then
        assertNotNull(account);
        assertInstanceOf(SavingsAccount.class, account);
        assertEquals(ACCOUNT_NUMBER, account.getAccountNumber());
        assertEquals(GRANTOR, account.getAccountHolderName());
        assertEquals(5000.0, account.getBalance());
    }


    @Test
    void shouldPreserveAllFieldsInRoundTripConversionForPaymentAccount() {
        // Given
        val originalAccount = new PaymentAccount(
                ACCOUNT_NUMBER,
                GRANTOR,
                2500.50
        );

        // When
        val document = AccountMapper.toDocument(originalAccount);
        val reconvertedAccount = AccountMapper.toDomain(document);

        // Then
        assertEquals(originalAccount.getAccountNumber(), reconvertedAccount.getAccountNumber());
        assertEquals(originalAccount.getAccountHolderName(), reconvertedAccount.getAccountHolderName());
        assertEquals(originalAccount.getBalance(), reconvertedAccount.getBalance());
        assertInstanceOf(PaymentAccount.class, reconvertedAccount);
    }

    @Test
    void shouldPreserveAllFieldsInRoundTripConversionForSavingsAccount() {
        // Given
        val originalAccount = new SavingsAccount(
                ACCOUNT_NUMBER,
                GRANTOR,
                10000.75
        );

        // When
        val document = AccountMapper.toDocument(originalAccount);
        val reconvertedAccount = AccountMapper.toDomain(document);

        // Then
        assertEquals(originalAccount.getAccountNumber(), reconvertedAccount.getAccountNumber());
        assertEquals(originalAccount.getAccountHolderName(), reconvertedAccount.getAccountHolderName());
        assertEquals(originalAccount.getBalance(), reconvertedAccount.getBalance());
        assertInstanceOf(SavingsAccount.class, reconvertedAccount);
    }
}

