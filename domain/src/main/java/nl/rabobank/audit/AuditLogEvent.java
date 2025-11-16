package nl.rabobank.audit;

import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;

import java.time.Instant;

public record AuditLogEvent(AuditLog auditLog) {
    public static AuditLogEvent of(AuditLog auditLog) {
        return new AuditLogEvent(auditLog);
    }

    public static AuditLog created(String id, Instant createdAt, PowerOfAttorney poa) {
        return AuditLog.builder()
                .id(id)
                .actorName(poa.grantorName())
                .granteeName(poa.granteeName())
                .updateType(UpdateType.CREATED)
                .poaId(poa.id())
                .accountNumber(poa.account().getAccountNumber())
                .newAuthorization(poa.authorization())
                .oldAuthorization(null)
                .createdAt(createdAt)
                .build();
    }

    public static AuditLog updated(String id, Instant createdAt, Authorization oldAuthorization, PowerOfAttorney newPoa) {
        return AuditLog.builder()
                .id(id)
                .actorName(newPoa.grantorName())
                .granteeName(newPoa.granteeName())
                .updateType(UpdateType.UPDATED)
                .poaId(newPoa.id())
                .accountNumber(newPoa.account().getAccountNumber())
                .newAuthorization(newPoa.authorization())
                .oldAuthorization(oldAuthorization)
                .createdAt(createdAt)
                .build();
    }

    public static AuditLog deleted(String id, Instant createdAt, PowerOfAttorney poa) {
        return AuditLog.builder()
                .id(id)
                .actorName(poa.grantorName())
                .granteeName(poa.granteeName())
                .updateType(UpdateType.DELETED)
                .poaId(poa.id())
                .accountNumber(poa.account().getAccountNumber())
                .newAuthorization(null)
                .oldAuthorization(poa.authorization())
                .createdAt(createdAt)
                .build();
    }
}
