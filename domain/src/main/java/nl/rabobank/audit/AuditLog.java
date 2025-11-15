package nl.rabobank.audit;

import nl.rabobank.authorizations.Authorization;

import java.time.Instant;

public record AuditLog(
        String id,
        String actorName,
        String granteeName,
        UpdateType updateType,
        String paoId,
        String accountNumber,
        Authorization newAuthorization,
        Authorization oldAuthorization,
        Instant createdAt
) {
}
