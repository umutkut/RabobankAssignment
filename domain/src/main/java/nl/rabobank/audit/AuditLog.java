package nl.rabobank.audit;

import lombok.Builder;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;

import java.time.Instant;

@Builder(toBuilder = true)
public record AuditLog(
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
    public static AuditLog created(String id, Instant createdAt, PowerOfAttorney poa) {
        return AuditLog.builder()
                .id(id)
                .actorName(poa.grantorName())
                .granteeName(poa.granteeName())
                .updateType(UpdateType.CREATED)
                .poaId(poa.id())
                .accountNumber(poa.account().accountNumber())
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
                .accountNumber(newPoa.account().accountNumber())
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
                .accountNumber(poa.account().accountNumber())
                .newAuthorization(null)
                .oldAuthorization(poa.authorization())
                .createdAt(createdAt)
                .build();
    }
}
