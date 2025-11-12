package nl.rabobank.mongo.documents;

import nl.rabobank.account.Account;
import nl.rabobank.account.PaymentAccount;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PowerOfAttorneyMapperTest {

    public static final String SOME_ACCOUNT_NUMBER = "NL91RABO1234567890";
    public static final String SOME_NAME = "Cheddar Dincer Tanis";
    public static final String SOME_OTHER_NAME = "Merkur Dincer Tanis";

    @Test
    void shouldNotInstantiateUtilityClass() {
        // When/Then
        assertThrows(IllegalStateException.class, PowerOfAttorneyMapper::new);
    }

    @Test
    void shouldConvertDomainToDocument() {
        // Given
        Account account = new PaymentAccount(
                SOME_ACCOUNT_NUMBER,
                SOME_NAME,
                1000.0
        );
        PowerOfAttorney powerOfAttorney = PowerOfAttorney.builder()
                .grantorName(SOME_NAME)
                .granteeName(SOME_OTHER_NAME)
                .account(account)
                .authorization(Authorization.READ)
                .build();

        // When
        LocalDateTime beforeCreation = LocalDateTime.now();
        PowerOfAttorneyDocument document = PowerOfAttorneyMapper.toDocument(powerOfAttorney);
        LocalDateTime afterCreation = LocalDateTime.now();

        // Then
        assertNotNull(document);
        assertEquals(SOME_NAME, document.getGrantorName());
        assertEquals(SOME_OTHER_NAME, document.getGranteeName());
        assertEquals(SOME_ACCOUNT_NUMBER, document.getAccountNumber());
        assertEquals(AuthorizationType.READ, document.getAuthorizationType());
        assertFalse(document.isRevoked());
        assertNotNull(document.getCreatedAt());
        assertTrue(document.getCreatedAt().isAfter(beforeCreation.minusSeconds(1)));
        assertTrue(document.getCreatedAt().isBefore(afterCreation.plusSeconds(1)));
    }

    @Test
    void shouldConvertDocumentToDomain() {
        // Given
        Account account = new PaymentAccount(
                SOME_ACCOUNT_NUMBER,
                SOME_NAME,
                1000.0
        );
        PowerOfAttorneyDocument document = PowerOfAttorneyDocument.builder()
                .id(SOME_ACCOUNT_NUMBER)
                .grantorName(SOME_NAME)
                .granteeName(SOME_OTHER_NAME)
                .accountNumber(SOME_ACCOUNT_NUMBER)
                .authorizationType(AuthorizationType.READ)
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .build();

        // When
        PowerOfAttorney powerOfAttorney = PowerOfAttorneyMapper.toDomain(document, account);

        // Then
        assertNotNull(powerOfAttorney);
        assertEquals(SOME_NAME, powerOfAttorney.getGrantorName());
        assertEquals(SOME_OTHER_NAME, powerOfAttorney.getGranteeName());
        assertEquals(account, powerOfAttorney.getAccount());
        assertEquals(SOME_ACCOUNT_NUMBER, powerOfAttorney.getAccount().getAccountNumber());
        assertEquals(Authorization.READ, powerOfAttorney.getAuthorization());
    }


    @Test
    void shouldPreserveAllFieldsInRoundTripConversion() {
        // Given
        Account account = new PaymentAccount(
                SOME_ACCOUNT_NUMBER,
                SOME_NAME,
                2500.0
        );
        PowerOfAttorney originalPowerOfAttorney = PowerOfAttorney.builder()
                .grantorName(SOME_NAME)
                .granteeName(SOME_OTHER_NAME)
                .account(account)
                .authorization(Authorization.READ)
                .build();

        // When
        PowerOfAttorneyDocument document = PowerOfAttorneyMapper.toDocument(originalPowerOfAttorney);
        PowerOfAttorney reconvertedPowerOfAttorney = PowerOfAttorneyMapper.toDomain(document, account);

        // Then
        assertEquals(originalPowerOfAttorney.getGrantorName(), reconvertedPowerOfAttorney.getGrantorName());
        assertEquals(originalPowerOfAttorney.getGranteeName(), reconvertedPowerOfAttorney.getGranteeName());
        assertEquals(originalPowerOfAttorney.getAccount().getAccountNumber(),
                reconvertedPowerOfAttorney.getAccount().getAccountNumber());
        assertEquals(originalPowerOfAttorney.getAuthorization(), reconvertedPowerOfAttorney.getAuthorization());
    }
}

