package nl.rabobank.mongo.repository;

import lombok.val;
import nl.rabobank.mongo.EmbeddedMongoConfiguration;
import nl.rabobank.mongo.documents.account.AccountDocument;
import nl.rabobank.mongo.documents.account.PaymentAccountDocument;
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
@ContextConfiguration(classes = EmbeddedMongoConfiguration.class)
class AccountMongoRepositoryIT {

    @Autowired
    private AccountMongoRepository accountMongoRepository;

    @BeforeEach
    void clean() {
        accountMongoRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveAccountDocument() {
        // Given
        val doc = PaymentAccountDocument.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .accountHolderName(GRANTOR)
                .balance(1000.0)
                .build();

        // When
        accountMongoRepository.save(doc);
        val retrieved = accountMongoRepository.findById(ACCOUNT_NUMBER);

        // Then
        assertTrue(retrieved.isPresent());
        val saved = retrieved.get();
        assertEquals(ACCOUNT_NUMBER, saved.getAccountNumber());
        assertEquals(GRANTOR, saved.getAccountHolderName());
        assertEquals(1000.0, saved.getBalance());
    }

    @Test
    void shouldFindAllByAccountHolderNameIgnoreCase() {
        // Given
        val doc1 = PaymentAccountDocument.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .accountHolderName(GRANTOR)
                .balance(1000.0)
                .build();
        val doc2 = PaymentAccountDocument.builder()
                .accountNumber(OTHER_ACCOUNT_NUMBER)
                .accountHolderName(GRANTOR.toUpperCase())
                .balance(500.0)
                .build();
        accountMongoRepository.saveAll(List.of(doc1, doc2));

        // When
        val result = accountMongoRepository.findAllByAccountHolderNameIgnoreCase(GRANTOR);

        // Then
        assertEquals(2, result.size());
        val numbers = result.stream().map(AccountDocument::getAccountNumber).toList();
        assertTrue(numbers.containsAll(List.of(ACCOUNT_NUMBER, OTHER_ACCOUNT_NUMBER)));
    }

    @Test
    void shouldFindAllByAccountNumberIn() {
        // Given
        val doc1 = PaymentAccountDocument.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .accountHolderName(GRANTOR)
                .balance(1000.0)
                .build();
        val doc2 = PaymentAccountDocument.builder()
                .accountNumber(OTHER_ACCOUNT_NUMBER)
                .accountHolderName(GRANTOR)
                .balance(500.0)
                .build();
        accountMongoRepository.saveAll(List.of(doc1, doc2));

        // When
        val result = accountMongoRepository.findAllByAccountNumberIn(List.of(ACCOUNT_NUMBER, OTHER_ACCOUNT_NUMBER));

        // Then
        assertEquals(2, result.size());
        val numbers = result.stream().map(AccountDocument::getAccountNumber).toList();
        assertTrue(numbers.containsAll(List.of(ACCOUNT_NUMBER, OTHER_ACCOUNT_NUMBER)));
    }
}
