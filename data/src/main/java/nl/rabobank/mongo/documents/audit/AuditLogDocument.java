package nl.rabobank.mongo.documents.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.rabobank.mongo.documents.poa.AuthorizationType;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "audit_log")
@TypeAlias("AuditLogDocument")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDocument {
    @Id
    private String id;

    @Indexed
    private String actorName;

    private String granteeName;

    private UpdateTypeDocument updateType;

    private String paoId;

    @Indexed
    private String accountNumber;

    private AuthorizationType newAuthorization;

    private AuthorizationType oldAuthorization;

    private Instant createdAt;
}
