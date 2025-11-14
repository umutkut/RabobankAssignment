package nl.rabobank.mongo.mapper;

import lombok.val;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.mongo.documents.poa.AuthorizationType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static nl.rabobank.mongo.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class PowerOfAttorneyMapperTest {



    @Test
    void shouldNotInstantiateUtilityClass() {
        // When/Then
        assertThrows(IllegalStateException.class, PowerOfAttorneyMapper::new);
    }

    @Test
    void shouldConvertDomainToDocument() {
        // Given
        val powerOfAttorney = givenPowerOfAttorney();

        // When
        val beforeCreation = LocalDateTime.now();
        val document = PowerOfAttorneyMapper.toDocument(powerOfAttorney);
        val afterCreation = LocalDateTime.now();

        // Then
        assertNotNull(document);
        assertEquals(POA_ID, document.getId());
        assertEquals(GRANTOR, document.getGrantorName());
        assertEquals(GRANTEE, document.getGranteeName());
        assertEquals(ACCOUNT_NUMBER, document.getAccountNumber());
        assertEquals(AuthorizationType.READ, document.getAuthorizationType());
        assertNotNull(document.getCreatedAt());
        assertTrue(document.getCreatedAt().isAfter(beforeCreation.minusSeconds(1)));
        assertTrue(document.getCreatedAt().isBefore(afterCreation.plusSeconds(1)));
    }

    @Test
    void shouldConvertDocumentToDomain() {
        // Given
        val account = givenPaymentAccount();
        val document = givenPowerOfAttorneyDocument();

        // When
        val powerOfAttorney = PowerOfAttorneyMapper.toDomain(document, account);

        // Then
        assertNotNull(powerOfAttorney);
        assertEquals(POA_ID, powerOfAttorney.getId());
        assertEquals(GRANTOR, powerOfAttorney.getGrantorName());
        assertEquals(GRANTEE, powerOfAttorney.getGranteeName());
        assertEquals(account, powerOfAttorney.getAccount());
        assertEquals(ACCOUNT_NUMBER, powerOfAttorney.getAccount().getAccountNumber());
        assertEquals(Authorization.READ, powerOfAttorney.getAuthorization());
    }


    @Test
    void shouldPreserveAllFieldsInRoundTripConversion() {
        // Given
        val account = givenPaymentAccount();
        val originalPowerOfAttorney = givenPowerOfAttorney();

        // When
        val document = PowerOfAttorneyMapper.toDocument(originalPowerOfAttorney);
        val reconvertedPowerOfAttorney = PowerOfAttorneyMapper.toDomain(document, account);

        // Then
        assertEquals(originalPowerOfAttorney.getId(), reconvertedPowerOfAttorney.getId());
        assertEquals(originalPowerOfAttorney.getGrantorName(), reconvertedPowerOfAttorney.getGrantorName());
        assertEquals(originalPowerOfAttorney.getGranteeName(), reconvertedPowerOfAttorney.getGranteeName());
        assertEquals(originalPowerOfAttorney.getAccount().getAccountNumber(),
                reconvertedPowerOfAttorney.getAccount().getAccountNumber());
        assertEquals(originalPowerOfAttorney.getAuthorization(), reconvertedPowerOfAttorney.getAuthorization());
    }
}

