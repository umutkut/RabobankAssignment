package nl.rabobank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.rabobank.audit.AuditEventsPublisher;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.exception.AccountNotFoundException;
import nl.rabobank.exception.ForbiddenOperationException;
import nl.rabobank.exception.PowerOfAttorneyAlreadyExistException;
import nl.rabobank.repository.AccountRepository;
import nl.rabobank.repository.PowerOfAttorneyRepository;
import nl.rabobank.service.model.CreatePowerOfAttorneyServiceRequest;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreatePowerOfAttorneyService {
    private final AccountRepository accountRepository;
    private final PowerOfAttorneyRepository powerOfAttorneyRepository;
    private final IdGenerator idGenerator;
    private final Clock clock;
    private final AuditEventsPublisher auditEventsPublisher;

    public PowerOfAttorney create(CreatePowerOfAttorneyServiceRequest request) {
        log.debug("Creating POA for accountNumber: {}", request.accountNumber());
        val account = accountRepository
                .findByAccountNumber(request.accountNumber())
                .orElseThrow(() -> new AccountNotFoundException("With accountNumber: " + request.accountNumber()));

        if (!account.accountHolderName().equalsIgnoreCase(request.grantorName().trim())) {
            throw new ForbiddenOperationException("User cannot operate. " + request.grantorName() + " is not owner of the requested account.");
        }

        if (request.grantorName().trim().equalsIgnoreCase(request.granteeName().trim())) {
            throw new ForbiddenOperationException("User cannot grant access to him/herself.");
        }

        powerOfAttorneyRepository
                .findByGrantorAndGranteeAndAccountNumber(request.grantorName(),
                        request.granteeName(),
                        request.accountNumber())
                .ifPresent(poa -> {
                    throw new PowerOfAttorneyAlreadyExistException();
                });

        val now = clock.instant();
        val powerOfAttorney = PowerOfAttorney.builder()
                .id(idGenerator.generateUUID())
                .account(account)
                .granteeName(request.granteeName())
                .grantorName(request.grantorName())
                .authorization(request.authorization())
                .createdAt(now)
                .updatedAt(now)
                .build();
        val savedPoa = powerOfAttorneyRepository.save(powerOfAttorney);

        log.debug("Created POA for accountNumber: {}", savedPoa.account().accountNumber());
        auditEventsPublisher.publishCreated(savedPoa);
        return savedPoa;
    }
}

