package nl.rabobank.mongo.mapper;

import nl.rabobank.audit.AuditLog;
import nl.rabobank.audit.UpdateType;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.mongo.documents.audit.AuditLogDocument;
import nl.rabobank.mongo.documents.audit.UpdateTypeDocument;
import nl.rabobank.mongo.documents.poa.AuthorizationType;

public class AuditLogMapper {
    AuditLogMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static AuditLogDocument toDocument(AuditLog auditLog) {
        return AuditLogDocument.builder()
                .id(auditLog.id())
                .actorName(auditLog.actorName())
                .granteeName(auditLog.granteeName())
                .updateType(mapToUpdateTypeDocument(auditLog.updateType()))
                .paoId(auditLog.paoId())
                .accountNumber(auditLog.accountNumber())
                .newAuthorization(mapToAuthorizationType(auditLog.newAuthorization()))
                .oldAuthorization(mapToAuthorizationType(auditLog.oldAuthorization()))
                .createdAt(auditLog.createdAt())
                .build();
    }

    public static AuditLog toDomain(AuditLogDocument document) {
        return new AuditLog(
                document.getId(),
                document.getActorName(),
                document.getGranteeName(),
                mapToUpdateType(document.getUpdateType()),
                document.getPaoId(),
                document.getAccountNumber(),
                mapToAuthorization(document.getNewAuthorization()),
                mapToAuthorization(document.getOldAuthorization()),
                document.getCreatedAt()
        );
    }

    private static AuthorizationType mapToAuthorizationType(Authorization authorization) {
        if (authorization == null) return null;
        return switch (authorization) {
            case READ -> AuthorizationType.READ;
            case WRITE -> AuthorizationType.WRITE;
        };
    }

    private static Authorization mapToAuthorization(AuthorizationType authorizationType) {
        if (authorizationType == null) return null;
        return switch (authorizationType) {
            case READ -> Authorization.READ;
            case WRITE -> Authorization.WRITE;
        };
    }

    private static UpdateTypeDocument mapToUpdateTypeDocument(UpdateType updateType) {
        if (updateType == null) return null;
        return switch (updateType) {
            case CREATED -> UpdateTypeDocument.CREATED;
            case UPDATED -> UpdateTypeDocument.UPDATED;
            case DELETED -> UpdateTypeDocument.DELETED;
        };
    }

    private static UpdateType mapToUpdateType(UpdateTypeDocument updateTypeDocument) {
        if (updateTypeDocument == null) return null;
        return switch (updateTypeDocument) {
            case CREATED -> UpdateType.CREATED;
            case UPDATED -> UpdateType.UPDATED;
            case DELETED -> UpdateType.DELETED;
        };
    }
}
