package nl.rabobank.repository;

import nl.rabobank.account.Account;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findAllByAccountHolderName(String accountHolderName);
}