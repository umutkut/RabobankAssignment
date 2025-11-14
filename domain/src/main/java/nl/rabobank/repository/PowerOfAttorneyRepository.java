package nl.rabobank.repository;

import nl.rabobank.authorizations.PowerOfAttorney;

import java.util.Optional;

public interface PowerOfAttorneyRepository {
    PowerOfAttorney save(PowerOfAttorney powerOfAttorney);

    Optional<PowerOfAttorney> findById(String id);
}