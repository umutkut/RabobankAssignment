package nl.rabobank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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

    public PowerOfAttorney updateAuthorization(UpdatePowerOfAttorneyAuthorizationRequest request) {
        log.debug("Updating authorization for POA id: {} to {}", request.paoId(), request.authorization());
        val poa = powerOfAttorneyRepository.findById(request.paoId())
                .orElseThrow(() -> new PowerOfAttorneyNotFoundException("With id: " + request.paoId()));

        val updated = poa.toBuilder()
                .authorization(request.authorization())
                .updatedAt(clock.instant())
                .build();

        val saved = powerOfAttorneyRepository.save(updated);
        log.debug("Updated authorization for POA id: {}", request.paoId());
        return saved;
    }
}
