package nl.rabobank.mongo.repositories;

import nl.rabobank.mongo.documents.PowerOfAttorneyDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PowerOfAttorneyRepository extends MongoRepository<PowerOfAttorneyDocument, String> {
}
