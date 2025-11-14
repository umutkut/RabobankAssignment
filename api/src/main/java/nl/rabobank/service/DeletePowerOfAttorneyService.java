package nl.rabobank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.rabobank.exception.ForbiddenOperationException;
import nl.rabobank.exception.PowerOfAttorneyNotFoundException;
import nl.rabobank.repository.PowerOfAttorneyRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeletePowerOfAttorneyService {
    private final PowerOfAttorneyRepository powerOfAttorneyRepository;

    public void deleteByIdAsGrantor(String poaId, String grantorName) {
        log.debug("Deleting POA with id: {} as grantor: {}", poaId, grantorName);
        val poa = powerOfAttorneyRepository.findById(poaId)
                .orElseThrow(() -> new PowerOfAttorneyNotFoundException("With id: " + poaId));

        if (!poa.grantorName().equals(grantorName)) {
            throw new ForbiddenOperationException("Only the grantor may delete this PoA");
        }

        powerOfAttorneyRepository.deleteById(poaId);
        log.debug("Deleted POA with id: {} as grantor: {}", poaId, grantorName);
    }
}
