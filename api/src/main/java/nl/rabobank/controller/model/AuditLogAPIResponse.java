package nl.rabobank.controller.model;

import nl.rabobank.audit.AuditLog;
import nl.rabobank.audit.UpdateType;
import nl.rabobank.authorizations.Authorization;

import java.time.Instant;

public record AuditLogAPIResponse(
        String id,
        String actorName,
        String granteeName,
        UpdateType updateType,
        String poaId,
        String accountNumber,
        Authorization newAuthorization,
        Authorization oldAuthorization,
        Instant createdAt
) {
    public static AuditLogAPIResponse from(AuditLog log) {
        return new AuditLogAPIResponse(
                log.id(),
                log.actorName(),
                log.granteeName(),
                log.updateType(),
                log.poaId(),
                log.accountNumber(),
                log.newAuthorization(),
                log.oldAuthorization(),
                log.createdAt()
        );
    }
}
