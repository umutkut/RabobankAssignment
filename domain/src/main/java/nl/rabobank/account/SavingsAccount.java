package nl.rabobank.account;

import lombok.Builder;

@Builder(toBuilder = true)
public record SavingsAccount(String accountNumber, String accountHolderName, Double balance) implements Account {
}
