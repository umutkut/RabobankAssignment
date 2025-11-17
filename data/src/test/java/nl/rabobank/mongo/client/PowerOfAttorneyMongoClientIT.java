package nl.rabobank.mongo.client;

import lombok.val;
import nl.rabobank.mongo.EmbeddedMongoTestConfiguration;
import nl.rabobank.mongo.documents.poa.PowerOfAttorneyDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static nl.rabobank.mongo.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = EmbeddedMongoTestConfiguration.class)
class PowerOfAttorneyMongoClientIT {

    @Autowired
    private PowerOfAttorneyMongoClient powerOfAttorneyMongoClient;

    @BeforeEach
    void clean() {
        powerOfAttorneyMongoClient.deleteAll();
    }

    @Test
    void shouldSaveAndRetrievePoaDocument() {
        // Given
        val poa = givenPowerOfAttorneyDocument();

        // When
        val saved = powerOfAttorneyMongoClient.save(poa);
        val retrieved = powerOfAttorneyMongoClient.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        val doc = retrieved.get();
        assertEquals(poa.getId(), doc.getId());
        assertEquals(GRANTOR, doc.getGrantorName());
        assertEquals(GRANTEE, doc.getGranteeName());
        assertEquals(ACCOUNT_NUMBER, doc.getAccountNumber());
    }

    @Test
    void shouldFindByGranteeNameIgnoreCase() {
        // Given
        val poa1 = givenPowerOfAttorneyDocument();
        val poa2 = PowerOfAttorneyDocument.builder()
                .id("id-2")
                .grantorName(GRANTOR)
                .granteeName(GRANTEE.toUpperCase())
                .accountNumber(OTHER_ACCOUNT_NUMBER)
                .authorizationType(poa1.getAuthorizationType())
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();
        powerOfAttorneyMongoClient.saveAll(List.of(poa1, poa2));

        // When
        val result = powerOfAttorneyMongoClient.findByGranteeNameIgnoreCase(GRANTEE);

        // Then
        assertEquals(2, result.size());
        val accountNumbers = result.stream().map(PowerOfAttorneyDocument::getAccountNumber).toList();
        assertTrue(accountNumbers.containsAll(List.of(ACCOUNT_NUMBER, OTHER_ACCOUNT_NUMBER)));
    }

    @Test
    void shouldFindByGrantorNameIgnoreCase() {
        // Given
        val poa1 = givenPowerOfAttorneyDocument();
        val poa2 = PowerOfAttorneyDocument.builder()
                .id("id-3")
                .grantorName(GRANTOR.toUpperCase())
                .granteeName("Another Grantee")
                .accountNumber("NL00RABO0000000000")
                .authorizationType(poa1.getAuthorizationType())
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();
        powerOfAttorneyMongoClient.saveAll(List.of(poa1, poa2));

        // When
        val result = powerOfAttorneyMongoClient.findByGrantorNameIgnoreCase(GRANTOR);

        // Then
        assertEquals(2, result.size());
        val grantors = result.stream().map(PowerOfAttorneyDocument::getGrantorName).map(String::toLowerCase).toList();
        assertTrue(grantors.stream().allMatch(g -> g.equals(GRANTOR.toLowerCase())));
    }

    @Test
    void shouldFindByGrantorGranteeAndAccountNumber() {
        // Given
        val poa = givenPowerOfAttorneyDocument();
        powerOfAttorneyMongoClient.save(poa);

        // When
        val found = powerOfAttorneyMongoClient.findByGrantorNameIgnoreCaseAndGranteeNameIgnoreCaseAndAccountNumber(
                GRANTOR, GRANTEE, ACCOUNT_NUMBER
        );
        val notFound = powerOfAttorneyMongoClient.findByGrantorNameIgnoreCaseAndGranteeNameIgnoreCaseAndAccountNumber(
                GRANTOR, GRANTEE, "NL00RABO0000000000"
        );

        // Then
        assertTrue(found.isPresent());
        assertEquals(poa.getId(), found.get().getId());
        assertTrue(notFound.isEmpty());
    }
}