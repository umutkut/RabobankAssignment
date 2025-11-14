package nl.rabobank.mongo.client;

import nl.rabobank.mongo.documents.poa.PowerOfAttorneyDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PowerOfAttorneyMongoClient extends MongoRepository<PowerOfAttorneyDocument, String> {

    Page<PowerOfAttorneyDocument> findByGrantorName(String grantorName, Pageable pageable);

    Optional<PowerOfAttorneyDocument> findByGrantorNameAndGranteeNameAndAccountNumber(String grantor, String grantee, String accountNumber);

    Page<PowerOfAttorneyDocument> findByGranteeName(String granteeName, Pageable pageable);
}
