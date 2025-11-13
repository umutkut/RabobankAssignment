package nl.rabobank.mongo.repositories;

import nl.rabobank.mongo.documents.poa.PowerOfAttorneyDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PowerOfAttorneyRepository extends MongoRepository<PowerOfAttorneyDocument, String> {
    List<PowerOfAttorneyDocument> findByGranteeNameAndRevokedFalse(String granteeName);

    List<PowerOfAttorneyDocument> findByGrantorNameAndRevokedFalse(String grantorName);
}
