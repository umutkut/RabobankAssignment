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
        switch (account) {
            case PaymentAccount ignored -> type = "PAYMENT";
            case SavingsAccount ignored -> type = "SAVINGS";
            default -> type = "UNKNOWN";
        }

        return new AccountAPIResponse(
                account.accountNumber(),
                account.accountHolderName(),
                account.balance(),
                type
        );
    }
}
