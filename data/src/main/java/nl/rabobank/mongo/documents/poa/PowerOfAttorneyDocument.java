package nl.rabobank.mongo.documents.poa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "power_of_attorney")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PowerOfAttorneyDocument {
    @Id
    private String id;

    @Indexed
    private String grantorName;

    @Indexed
    private String granteeName;

    @Indexed
    private String accountNumber;

    private AuthorizationType authorizationType;

    private boolean revoked = false;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
}