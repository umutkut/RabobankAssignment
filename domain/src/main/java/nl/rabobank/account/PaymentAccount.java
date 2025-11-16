package nl.rabobank.account;

import lombok.Builder;

@Builder(toBuilder = true)
public record PaymentAccount(String accountNumber, String accountHolderName, Double balance) implements Account {
}
