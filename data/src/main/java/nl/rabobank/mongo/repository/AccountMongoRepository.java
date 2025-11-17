package nl.rabobank.mongo.repository;

import nl.rabobank.mongo.documents.account.AccountDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountMongoRepository extends MongoRepository<AccountDocument, String> {
    List<AccountDocument> findAllByAccountHolderNameIgnoreCase(String accountHolderName);

    List<AccountDocument> findAllByAccountNumberIn(List<String> accountNumbers);
}
