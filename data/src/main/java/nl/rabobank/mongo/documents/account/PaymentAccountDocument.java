package nl.rabobank.mongo.documents.account;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@TypeAlias("PaymentAccountDocument")
public final class PaymentAccountDocument extends AccountDocument {

    @Builder(toBuilder = true)
    public PaymentAccountDocument(String accountNumber,
                                  String accountHolderName, Double balance) {
        super(accountNumber, AccountType.PAYMENT, balance, accountHolderName);
    }
}
