package nl.rabobank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.repository.PowerOfAttorneyService;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetGrantorPowerOfAttorneyService {

    private final PowerOfAttorneyService powerOfAttorneyService;

    public List<PowerOfAttorney> listPoasForGrantor(String grantorName) {
        log.debug("Listing POAs created by grantor: {}", grantorName);
        final String normalized = grantorName != null ? grantorName.trim() : null;
        return powerOfAttorneyService.findByGrantorName(normalized);
    }
}
