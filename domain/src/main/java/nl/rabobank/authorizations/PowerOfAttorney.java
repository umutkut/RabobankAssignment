package nl.rabobank.authorizations;

import lombok.Builder;
import nl.rabobank.account.Account;

import java.time.Instant;

@Builder(toBuilder = true)
public record PowerOfAttorney(
        String id,
        String granteeName,
        String grantorName,
        Account account,
        Authorization authorization,
        Instant createdAt,
        Instant updatedAt
) {
}
