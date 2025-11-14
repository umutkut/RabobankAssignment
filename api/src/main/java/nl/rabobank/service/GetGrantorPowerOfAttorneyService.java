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
public class GetGrantorPowerOfAttorneyService {

    private final PowerOfAttorneyRepository powerOfAttorneyRepository;

    public Page<PowerOfAttorney> listPoasForGrantor(String grantorName, Pageable pageable) {
        log.debug("Listing POAs created by grantor: {} pageable: {}", grantorName, pageable);
        return powerOfAttorneyRepository.findByGrantorName(grantorName, pageable);
    }
}
