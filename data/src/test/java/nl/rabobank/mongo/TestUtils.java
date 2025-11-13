package nl.rabobank.mongo;

import nl.rabobank.account.PaymentAccount;
import nl.rabobank.account.SavingsAccount;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.mongo.documents.account.PaymentAccountDocument;
import nl.rabobank.mongo.documents.account.SavingsAccountDocument;
import nl.rabobank.mongo.documents.poa.AuthorizationType;
import nl.rabobank.mongo.documents.poa.PowerOfAttorneyDocument;

import java.time.LocalDateTime;

public class TestUtils {
    public static final String ACCOUNT_NUMBER = "NL91RABO1234567890";
    public static final String OTHER_ACCOUNT_NUMBER = "NL13RABO0987654321";
    public static final String GRANTOR = "Cheddar Dincer Tanis";
    public static final String GRANTEE = "Merkur Dincer Tanis";
    public static final String POA_ID = "id-1";
    public static final Double BALANCE = 1000.0;

    public static PaymentAccount givenPaymentAccount() {
        return new PaymentAccount(
                ACCOUNT_NUMBER,
                GRANTOR,
                BALANCE
        );
    }

    public static SavingsAccount givenSavingsAccount() {
        return new SavingsAccount(
                ACCOUNT_NUMBER,
                GRANTOR,
                BALANCE
        );
    }

    public static PaymentAccountDocument givenPaymentAccountDocument() {
        return PaymentAccountDocument.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .accountHolderName(GRANTOR)
                .balance(BALANCE)
                .build();
    }

    public static SavingsAccountDocument givenSavingsAccountDocument() {
        return SavingsAccountDocument.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .accountHolderName(GRANTOR)
                .balance(BALANCE)
                .build();
    }

    public static PowerOfAttorneyDocument givenPowerOfAttorneyDocument() {
        return PowerOfAttorneyDocument.builder()
                .id(POA_ID)
                .grantorName(GRANTOR)
                .granteeName(GRANTEE)
                .accountNumber(ACCOUNT_NUMBER)
                .authorizationType(AuthorizationType.READ)
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static PowerOfAttorney givenPowerOfAttorney() {
        return PowerOfAttorney.builder()
                .id(POA_ID)
                .grantorName(GRANTOR)
                .granteeName(GRANTEE)
                .account(givenPaymentAccount())
                .authorization(Authorization.READ)
                .build();
    }
}
