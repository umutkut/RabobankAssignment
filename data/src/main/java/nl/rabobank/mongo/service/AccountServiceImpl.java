package nl.rabobank.mongo.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import nl.rabobank.account.Account;
import nl.rabobank.mongo.mapper.AccountMapper;
import nl.rabobank.mongo.repository.AccountMongoRepository;
import nl.rabobank.repository.AccountService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountMongoRepository accountMongoRepository;

    public Optional<Account> findByAccountNumber(String accountNumber) {
        val optDoc = accountMongoRepository.findById(accountNumber);
        return optDoc.map(AccountMapper::toDomain);
    }

    @Override
    public List<Account> findAllByAccountHolderName(String accountHolderName) {
        val docs = accountMongoRepository.findAllByAccountHolderNameIgnoreCase(accountHolderName);
        return docs.stream().map(AccountMapper::toDomain).toList();
    }
}
