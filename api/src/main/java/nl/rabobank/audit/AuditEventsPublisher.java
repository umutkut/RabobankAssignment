package nl.rabobank.audit;

import lombok.RequiredArgsConstructor;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.service.IdGenerator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
@RequiredArgsConstructor
public class AuditEventsPublisher {
    private final ApplicationEventPublisher springEvents;
    private final IdGenerator idGenerator;
    private final Clock clock;

    // Sake of simplicity we use in-process Spring events. Note: this can lose events on crash.

    public void publishCreated(PowerOfAttorney poa) {
        springEvents.publishEvent(AuditLogEvent.of(
                AuditLog.created(idGenerator.generateUUID(), clock.instant(), poa)
        ));
    }

    public void publishUpdated(Authorization oldAuthorization, PowerOfAttorney poa) {
        springEvents.publishEvent(AuditLogEvent.of(
                AuditLog.updated(idGenerator.generateUUID(), clock.instant(), oldAuthorization, poa)
        ));
    }

    public void publishDeleted(PowerOfAttorney poa) {
        springEvents.publishEvent(AuditLogEvent.of(
                AuditLog.deleted(idGenerator.generateUUID(), clock.instant(), poa)
        ));
    }
}
