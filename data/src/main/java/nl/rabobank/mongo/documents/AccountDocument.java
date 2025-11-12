package nl.rabobank.mongo.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public sealed class AccountDocument {

    @Id
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;

    @Indexed
    private String accountHolderName;
}

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@TypeAlias("PaymentAccountDocument")
final class PaymentAccountDocument extends AccountDocument {

    @Builder
    public PaymentAccountDocument(String accountNumber,
                                  String accountHolderName, BigDecimal balance) {
        super(accountNumber, AccountType.PAYMENT, balance, accountHolderName);
    }
}

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@TypeAlias("SavingsAccountDocument")
final class SavingsAccountDocument extends AccountDocument {

    @Builder
    public SavingsAccountDocument(String accountNumber,
                                  String accountHolderName, BigDecimal balance) {
        super(accountNumber, AccountType.SAVINGS, balance, accountHolderName);
    }
}