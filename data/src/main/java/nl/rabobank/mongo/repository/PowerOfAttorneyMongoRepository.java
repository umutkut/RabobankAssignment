package nl.rabobank.mongo.repository;

import nl.rabobank.mongo.documents.poa.PowerOfAttorneyDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PowerOfAttorneyMongoRepository extends MongoRepository<PowerOfAttorneyDocument, String> {

    List<PowerOfAttorneyDocument> findByGrantorNameIgnoreCase(String grantorName);

    Optional<PowerOfAttorneyDocument> findByGrantorNameIgnoreCaseAndGranteeNameIgnoreCaseAndAccountNumber(String grantor, String grantee, String accountNumber);

    List<PowerOfAttorneyDocument> findByGranteeNameIgnoreCase(String granteeName);
}
