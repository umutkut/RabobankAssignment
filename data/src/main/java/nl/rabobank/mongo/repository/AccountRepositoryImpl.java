package nl.rabobank.mongo.repository;

import lombok.RequiredArgsConstructor;
import lombok.val;
import nl.rabobank.account.Account;
import nl.rabobank.mongo.client.AccountMongoClient;
import nl.rabobank.mongo.mapper.AccountMapper;
import nl.rabobank.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountMongoClient accountMongoClient;

    public Optional<Account> findByAccountNumber(String accountNumber) {
        val optDoc = accountMongoClient.findById(accountNumber);
        return optDoc.map(AccountMapper::toDomain);
    }
}
