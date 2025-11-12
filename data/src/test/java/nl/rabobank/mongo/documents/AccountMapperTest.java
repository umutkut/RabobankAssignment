package nl.rabobank.mongo.documents;

import nl.rabobank.account.Account;
import nl.rabobank.account.PaymentAccount;
import nl.rabobank.account.SavingsAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountMapperTest {

    public static final String SOME_ACCOUNT_NUMBER = "NL91RABO1234567890";
    public static final String SOME_NAME = "Cheddar Dincer Tanis";

    @Test
    void shouldNotInstantiateUtilityClass() {
        // When/Then
        assertThrows(IllegalStateException.class, AccountMapper::new);
    }

    @Test
    void shouldConvertPaymentAccountToDocument() {
        // Given
        PaymentAccount paymentAccount = new PaymentAccount(
                SOME_ACCOUNT_NUMBER,
                SOME_NAME,
                1000.0
        );

        // When
        AccountDocument document = AccountMapper.toDocument(paymentAccount);

        // Then
        assertNotNull(document);
        assertInstanceOf(PaymentAccountDocument.class, document);
        assertEquals(SOME_ACCOUNT_NUMBER, document.getAccountNumber());
        assertEquals(SOME_NAME, document.getAccountHolderName());
        assertEquals(1000.0, document.getBalance());
        assertEquals(AccountType.PAYMENT, document.getAccountType());
    }

    @Test
    void shouldConvertSavingsAccountToDocument() {
        // Given
        SavingsAccount savingsAccount = new SavingsAccount(
                SOME_ACCOUNT_NUMBER,
                SOME_NAME,
                5000.0
        );

        // When
        AccountDocument document = AccountMapper.toDocument(savingsAccount);

        // Then
        assertNotNull(document);
        assertInstanceOf(SavingsAccountDocument.class, document);
        assertEquals(SOME_ACCOUNT_NUMBER, document.getAccountNumber());
        assertEquals(SOME_NAME, document.getAccountHolderName());
        assertEquals(5000.0, document.getBalance());
        assertEquals(AccountType.SAVINGS, document.getAccountType());
    }

    @Test
    void shouldThrowExceptionForUnknownAccountType() {
        // Given
        Account unknownAccount = new Account() {
            @Override
            public String getAccountNumber() {
                return SOME_ACCOUNT_NUMBER;
            }

            @Override
            public String getAccountHolderName() {
                return SOME_NAME;
            }

            @Override
            public Double getBalance() {
                return 100.0;
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
        AccountDocument document = PaymentAccountDocument.builder()
                .accountNumber(SOME_ACCOUNT_NUMBER)
                .accountHolderName(SOME_NAME)
                .balance(1000.0)
                .build();

        // When
        Account account = AccountMapper.toDomain(document);

        // Then
        assertNotNull(account);
        assertInstanceOf(PaymentAccount.class, account);
        assertEquals(SOME_ACCOUNT_NUMBER, account.getAccountNumber());
        assertEquals(SOME_NAME, account.getAccountHolderName());
        assertEquals(1000.0, account.getBalance());
    }

    @Test
    void shouldConvertSavingsAccountDocumentToDomain() {
        // Given
        AccountDocument document = SavingsAccountDocument.builder()
                .accountNumber(SOME_ACCOUNT_NUMBER)
                .accountHolderName(SOME_NAME)
                .balance(5000.0)
                .build();

        // When
        Account account = AccountMapper.toDomain(document);

        // Then
        assertNotNull(account);
        assertInstanceOf(SavingsAccount.class, account);
        assertEquals(SOME_ACCOUNT_NUMBER, account.getAccountNumber());
        assertEquals(SOME_NAME, account.getAccountHolderName());
        assertEquals(5000.0, account.getBalance());
    }


    @Test
    void shouldPreserveAllFieldsInRoundTripConversionForPaymentAccount() {
        // Given
        PaymentAccount originalAccount = new PaymentAccount(
                SOME_ACCOUNT_NUMBER,
                SOME_NAME,
                2500.50
        );

        // When
        AccountDocument document = AccountMapper.toDocument(originalAccount);
        Account reconvertedAccount = AccountMapper.toDomain(document);

        // Then
        assertEquals(originalAccount.getAccountNumber(), reconvertedAccount.getAccountNumber());
        assertEquals(originalAccount.getAccountHolderName(), reconvertedAccount.getAccountHolderName());
        assertEquals(originalAccount.getBalance(), reconvertedAccount.getBalance());
        assertInstanceOf(PaymentAccount.class, reconvertedAccount);
    }

    @Test
    void shouldPreserveAllFieldsInRoundTripConversionForSavingsAccount() {
        // Given
        SavingsAccount originalAccount = new SavingsAccount(
                SOME_ACCOUNT_NUMBER,
                SOME_NAME,
                10000.75
        );

        // When
        AccountDocument document = AccountMapper.toDocument(originalAccount);
        Account reconvertedAccount = AccountMapper.toDomain(document);

        // Then
        assertEquals(originalAccount.getAccountNumber(), reconvertedAccount.getAccountNumber());
        assertEquals(originalAccount.getAccountHolderName(), reconvertedAccount.getAccountHolderName());
        assertEquals(originalAccount.getBalance(), reconvertedAccount.getBalance());
        assertInstanceOf(SavingsAccount.class, reconvertedAccount);
    }
}

