package nl.rabobank.mongo.mapper;

import lombok.val;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.mongo.documents.poa.AuthorizationType;
import org.junit.jupiter.api.Test;

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
        val document = PowerOfAttorneyMapper.toDocument(powerOfAttorney);

        // Then
        assertNotNull(document);
        assertEquals(POA_ID, document.getId());
        assertEquals(GRANTOR, document.getGrantorName());
        assertEquals(GRANTEE, document.getGranteeName());
        assertEquals(ACCOUNT_NUMBER, document.getAccountNumber());
        assertEquals(AuthorizationType.READ, document.getAuthorizationType());
        assertEquals(CREATED_AT, document.getCreatedAt());
        assertEquals(UPDATED_AT, document.getUpdatedAt());
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
        assertEquals(POA_ID, powerOfAttorney.id());
        assertEquals(GRANTOR, powerOfAttorney.grantorName());
        assertEquals(GRANTEE, powerOfAttorney.granteeName());
        assertEquals(account, powerOfAttorney.account());
        assertEquals(ACCOUNT_NUMBER, powerOfAttorney.account().getAccountNumber());
        assertEquals(Authorization.READ, powerOfAttorney.authorization());
        assertEquals(CREATED_AT, powerOfAttorney.createdAt());
        assertEquals(UPDATED_AT, powerOfAttorney.updatedAt());
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
        assertEquals(originalPowerOfAttorney.id(), reconvertedPowerOfAttorney.id());
        assertEquals(originalPowerOfAttorney.grantorName(), reconvertedPowerOfAttorney.grantorName());
        assertEquals(originalPowerOfAttorney.granteeName(), reconvertedPowerOfAttorney.granteeName());
        assertEquals(originalPowerOfAttorney.account().getAccountNumber(),
                reconvertedPowerOfAttorney.account().getAccountNumber());
        assertEquals(originalPowerOfAttorney.authorization(), reconvertedPowerOfAttorney.authorization());
        assertEquals(originalPowerOfAttorney.createdAt(), reconvertedPowerOfAttorney.createdAt());
        assertEquals(originalPowerOfAttorney.updatedAt(), reconvertedPowerOfAttorney.updatedAt());
    }
}

