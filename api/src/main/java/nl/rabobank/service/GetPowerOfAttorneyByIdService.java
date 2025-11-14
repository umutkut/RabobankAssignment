package nl.rabobank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.exception.PowerOfAttorneyNotFoundException;
import nl.rabobank.repository.PowerOfAttorneyRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetPowerOfAttorneyByIdService {

    private final PowerOfAttorneyRepository powerOfAttorneyRepository;

    public PowerOfAttorney getById(String id) {
        log.debug("Fetching POA by id: {}", id);
        val poa = powerOfAttorneyRepository.findById(id)
                .orElseThrow(() -> new PowerOfAttorneyNotFoundException("With id: " + id));
        log.debug("Found POA for id: {}", id);
        return poa;
    }
}
