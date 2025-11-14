package nl.rabobank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.repository.PowerOfAttorneyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetGranteePowerOfAttorneyService {

    private final PowerOfAttorneyRepository powerOfAttorneyRepository;

    public Page<PowerOfAttorney> listPoasForUser(String granteeName, Pageable pageable) {
        log.debug("Listing accessible POAs for grantee: {} pageable: {}", granteeName, pageable);
        return powerOfAttorneyRepository.findByGranteeName(granteeName, pageable);
    }
}
