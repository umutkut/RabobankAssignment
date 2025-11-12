package nl.rabobank.mongo.repositories;

import nl.rabobank.mongo.documents.AccountDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends MongoRepository<AccountDocument, String> {
    
}
