package nl.rabobank.mongo.client;

import nl.rabobank.mongo.documents.poa.PowerOfAttorneyDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PowerOfAttorneyMongoClient extends MongoRepository<PowerOfAttorneyDocument, String> {
    List<PowerOfAttorneyDocument> findByGranteeName(String granteeName);

    List<PowerOfAttorneyDocument> findByGrantorName(String grantorName);

    Optional<PowerOfAttorneyDocument> findByGrantorNameAndGranteeNameAndAccountNumber(String grantor, String grantee, String accountNumber);
}
