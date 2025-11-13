package nl.rabobank.mongo.documents.account;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@TypeAlias("SavingsAccountDocument")
public final class SavingsAccountDocument extends AccountDocument {

    @Builder(toBuilder = true)
    public SavingsAccountDocument(String accountNumber,
                                  String accountHolderName, Double balance) {
        super(accountNumber, AccountType.SAVINGS, balance, accountHolderName);
    }
}
