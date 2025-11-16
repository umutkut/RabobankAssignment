package nl.rabobank.repository;

import nl.rabobank.authorizations.PowerOfAttorney;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PowerOfAttorneyRepository {
    PowerOfAttorney save(PowerOfAttorney powerOfAttorney);

    Optional<PowerOfAttorney> findById(String id);

    Optional<PowerOfAttorney> findByGrantorAndGranteeAndAccountNumber(String grantor, String grantee, String accountNumber);

    List<PowerOfAttorney> findByGranteeName(String granteeName);

    Page<PowerOfAttorney> findByGrantorName(String grantorName, Pageable pageable);

    void deleteById(String id);
}