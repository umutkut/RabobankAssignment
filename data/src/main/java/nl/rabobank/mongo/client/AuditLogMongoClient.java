package nl.rabobank.mongo.client;

import nl.rabobank.mongo.documents.audit.AuditLogDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogMongoClient extends MongoRepository<AuditLogDocument, String> {
    Page<AuditLogDocument> findByAccountNumber(String accountNumber, Pageable pageable);

    Page<AuditLogDocument> findByActorName(String actorName, Pageable pageable);
}
