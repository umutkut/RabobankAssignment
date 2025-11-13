package nl.rabobank.mongo.repositories;

import nl.rabobank.mongo.documents.account.AccountDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends MongoRepository<AccountDocument, String> {
    List<AccountDocument> findAllByAccountHolderName(String accountHolderName);

    List<AccountDocument> findAllByAccountNumberIn(List<String> accountNumbers);
}
