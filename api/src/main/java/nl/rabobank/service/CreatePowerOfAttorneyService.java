package nl.rabobank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.exception.AccountNotFoundException;
import nl.rabobank.exception.PowerOfAttorneyAlreadyExistException;
import nl.rabobank.exception.UnsupportedUserOperationException;
import nl.rabobank.repository.AccountRepository;
import nl.rabobank.repository.PowerOfAttorneyRepository;
import nl.rabobank.service.model.CreatePowerOfAttorneyServiceRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreatePowerOfAttorneyService {
    private final AccountRepository accountRepository;
    private final PowerOfAttorneyRepository powerOfAttorneyRepository;
    private final IdGenerator idGenerator;

    public PowerOfAttorney create(CreatePowerOfAttorneyServiceRequest request) {
        log.debug("Creating POA for accountNumber: {}", request.accountNumber());
        val account = accountRepository
                .findByAccountNumber(request.accountNumber())
                .orElseThrow(() -> new AccountNotFoundException("With accountNumber: " + request.accountNumber()));

        if (!account.getAccountHolderName().equals(request.grantorName())) {
            throw new UnsupportedUserOperationException(request.grantorName() + " is not owner of the requested account.");
        }

        powerOfAttorneyRepository
                .findByGrantorAndGranteeAndAccountNumber(request.grantorName(),
                        request.granteeName(),
                        request.accountNumber())
                .ifPresent(poa -> {
                    throw new PowerOfAttorneyAlreadyExistException();
                });

        val now = Instant.now();
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

        log.debug("Created POA for accountNumber: {}", savedPoa.account().getAccountNumber());
        return savedPoa;
    }
}

