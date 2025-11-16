package nl.rabobank.mongo.mapper;

import nl.rabobank.account.Account;
import nl.rabobank.account.PaymentAccount;
import nl.rabobank.account.SavingsAccount;
import nl.rabobank.mongo.documents.account.AccountDocument;
import nl.rabobank.mongo.documents.account.PaymentAccountDocument;
import nl.rabobank.mongo.documents.account.SavingsAccountDocument;

public class AccountMapper {
    AccountMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static AccountDocument toDocument(Account account) {
        if (account instanceof PaymentAccount(String accountNumber, String accountHolderName, Double balance)) {
            return PaymentAccountDocument.builder()
                    .accountNumber(accountNumber)
                    .accountHolderName(accountHolderName)
                    .balance(balance)
                    .build();
        } else if (account instanceof SavingsAccount(String accountNumber, String accountHolderName, Double balance)) {
            return SavingsAccountDocument.builder()
                    .accountNumber(accountNumber)
                    .accountHolderName(accountHolderName)
                    .balance(balance)
                    .build();
        }
        throw new IllegalArgumentException("Unknown account type");
    }

    public static Account toDomain(AccountDocument document) {
        return switch (document.getAccountType()) {
            case PAYMENT -> new PaymentAccount(
                    document.getAccountNumber(),
                    document.getAccountHolderName(),
                    document.getBalance()
            );
            case SAVINGS -> new SavingsAccount(
                    document.getAccountNumber(),
                    document.getAccountHolderName(),
                    document.getBalance()
            );
        };
    }
}

