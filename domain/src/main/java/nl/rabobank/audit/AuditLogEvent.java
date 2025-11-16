package nl.rabobank.audit;

public record AuditLogEvent(AuditLog auditLog) {
    public static AuditLogEvent of(AuditLog auditLog) {
        return new AuditLogEvent(auditLog);
    }
}
