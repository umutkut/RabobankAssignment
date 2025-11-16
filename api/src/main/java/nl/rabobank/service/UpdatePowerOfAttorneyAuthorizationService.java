package nl.rabobank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.rabobank.audit.AuditEventsPublisher;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.exception.PowerOfAttorneyNotFoundException;
import nl.rabobank.repository.PowerOfAttorneyRepository;
import nl.rabobank.service.model.UpdatePowerOfAttorneyAuthorizationRequest;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdatePowerOfAttorneyAuthorizationService {

    private final PowerOfAttorneyRepository powerOfAttorneyRepository;
    private final Clock clock;
    private final AuditEventsPublisher auditEventsPublisher;

    public PowerOfAttorney updateAuthorization(UpdatePowerOfAttorneyAuthorizationRequest request) {
        log.debug("Updating authorization for POA id: {} to {}", request.paoId(), request.authorization());
        val poa = powerOfAttorneyRepository.findById(request.paoId())
                .orElseThrow(() -> new PowerOfAttorneyNotFoundException("With id: " + request.paoId()));

        if (poa.authorization() == request.authorization()) {
            log.debug("Authorization is already set to {}", request.authorization());
            return poa;
        }

        val updatedPoa = poa.toBuilder()
                .authorization(request.authorization())
                .updatedAt(clock.instant())
                .build();

        val saved = powerOfAttorneyRepository.save(updatedPoa);
        log.debug("Updated authorization for POA id: {}", request.paoId());

        auditEventsPublisher.publishUpdated(poa.authorization(), updatedPoa);
        return saved;
    }
}
