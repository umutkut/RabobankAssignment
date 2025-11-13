package nl.rabobank.controller.model;

import nl.rabobank.account.Account;
import nl.rabobank.account.PaymentAccount;
import nl.rabobank.account.SavingsAccount;

public record AccountAPIResponse(
        String accountNumber,
        String accountHolderName,
        Double balance,
        String type
) {
    public static AccountAPIResponse from(Account account) {
        String type;
        if (account instanceof PaymentAccount) {
            type = "PAYMENT";
        } else if (account instanceof SavingsAccount) {
            type = "SAVINGS";
        } else {
            type = "UNKNOWN";
        }
        return new AccountAPIResponse(
                account.getAccountNumber(),
                account.getAccountHolderName(),
                account.getBalance(),
                type
        );
    }
}
