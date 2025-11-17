package nl.rabobank.controller.model;

import nl.rabobank.account.Account;
import nl.rabobank.account.PaymentAccount;
import nl.rabobank.account.SavingsAccount;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.service.model.AccountWithAuthorization;

public record AccountWithAuthorizationAPIResponse(
        String accountNumber,
        String accountHolderName,
        Double balance,
        String type,
        Authorization authorization
) {
    public static AccountWithAuthorizationAPIResponse from(AccountWithAuthorization access) {
        Account account = access.account();
        String type;
        switch (account) {
            case PaymentAccount ignored -> type = "PAYMENT";
            case SavingsAccount ignored -> type = "SAVINGS";
            default -> type = "UNKNOWN";
        }

        return new AccountWithAuthorizationAPIResponse(
                account.accountNumber(),
                account.accountHolderName(),
                account.balance(),
                type,
                access.authorization()
        );
    }
}
