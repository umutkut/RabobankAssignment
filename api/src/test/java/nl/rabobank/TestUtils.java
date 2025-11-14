package nl.rabobank;

import nl.rabobank.account.PaymentAccount;
import nl.rabobank.account.SavingsAccount;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Files;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TestUtils {
    public static final String ACCOUNT_NUMBER = "NL91RABO1234567890";
    public static final String GRANTOR = "Cheddar Dincer Tanis";
    public static final String GRANTEE = "Merkur Dincer Tanis";
    public static final String POA_ID = "0899c184-1e66-46b4-ae0a-5b8536d6356b";
    public static final Double BALANCE = 1000.0;

    public static String readStringFromFile(String path) throws Exception {
        var resource = new ClassPathResource(path);
        return Files.readString(resource.getFile().toPath(), UTF_8);
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
}
