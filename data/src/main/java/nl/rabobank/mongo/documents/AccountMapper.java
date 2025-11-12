package nl.rabobank.mongo.documents;

import nl.rabobank.account.Account;
import nl.rabobank.account.PaymentAccount;
import nl.rabobank.account.SavingsAccount;

public class AccountMapper {
    AccountMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static AccountDocument toDocument(Account account) {
        if (account instanceof PaymentAccount payment) {
            return PaymentAccountDocument.builder()
                    .accountNumber(payment.getAccountNumber())
                    .accountHolderName(payment.getAccountHolderName())
                    .balance(payment.getBalance())
                    .build();
        } else if (account instanceof SavingsAccount savings) {
            return SavingsAccountDocument.builder()
                    .accountNumber(savings.getAccountNumber())
                    .accountHolderName(savings.getAccountHolderName())
                    .balance(savings.getBalance())
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

