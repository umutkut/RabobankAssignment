package nl.rabobank.controller.model;

import nl.rabobank.account.Account;
import nl.rabobank.account.PaymentAccount;
import nl.rabobank.account.SavingsAccount;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.mongo.documents.account.AccountType;
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
        if (account instanceof PaymentAccount) {
            type = AccountType.PAYMENT.name();
        } else if (account instanceof SavingsAccount) {
            type = AccountType.SAVINGS.name();
        } else {
            type = "UNKNOWN";
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
