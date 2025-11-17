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
        return switch (account) {
            case PaymentAccount paymentAccount -> PaymentAccountDocument.builder()
                    .accountNumber(paymentAccount.accountNumber())
                    .accountHolderName(paymentAccount.accountHolderName())
                    .balance(paymentAccount.balance())
                    .build();
            case SavingsAccount savingsAccount -> SavingsAccountDocument.builder()
                    .accountNumber(savingsAccount.accountNumber())
                    .accountHolderName(savingsAccount.accountHolderName())
                    .balance(savingsAccount.balance())
                    .build();
            default -> throw new IllegalArgumentException("Unknown account type");
        };
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

