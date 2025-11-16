package nl.rabobank.service.model;

import nl.rabobank.account.Account;
import nl.rabobank.authorizations.Authorization;

public record AccountWithAuthorization(
        Account account,
        Authorization authorization
) {
}
