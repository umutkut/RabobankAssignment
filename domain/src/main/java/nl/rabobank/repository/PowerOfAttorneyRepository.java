package nl.rabobank.repository;

import nl.rabobank.authorizations.PowerOfAttorney;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PowerOfAttorneyRepository {
    PowerOfAttorney save(PowerOfAttorney powerOfAttorney);

    Optional<PowerOfAttorney> findById(String id);

    Optional<PowerOfAttorney> findByGrantorAndGranteeAndAccountNumber(String grantor, String grantee, String accountNumber);

    Page<PowerOfAttorney> findByGranteeName(String granteeName, Pageable pageable);

    Page<PowerOfAttorney> findByGrantorName(String grantorName, Pageable pageable);

    void deleteById(String id);
}