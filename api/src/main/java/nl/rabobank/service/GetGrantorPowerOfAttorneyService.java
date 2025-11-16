package nl.rabobank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.repository.PowerOfAttorneyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetGrantorPowerOfAttorneyService {

    private final PowerOfAttorneyRepository powerOfAttorneyRepository;

    public List<PowerOfAttorney> listPoasForGrantor(String grantorName) {
        log.debug("Listing POAs created by grantor: {}", grantorName);
        return powerOfAttorneyRepository.findByGrantorName(grantorName);
    }
}
