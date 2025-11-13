package nl.rabobank.mongo.documents.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public sealed class AccountDocument permits PaymentAccountDocument, SavingsAccountDocument {

    @Id
    String accountNumber;
    AccountType accountType;
    Double balance;

    @Indexed
    String accountHolderName;
}

