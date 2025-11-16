package nl.rabobank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.rabobank.account.Account;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.repository.AccountRepository;
import nl.rabobank.repository.PowerOfAttorneyRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetAccountsAccessibleByUserService {

    private final PowerOfAttorneyRepository powerOfAttorneyRepository;
    private final AccountRepository accountRepository;

    public List<Account> listAccountsAccessibleByUser(String userName) {
        log.debug("Listing accessible accounts for user: {}", userName);

        val ownAccounts = accountRepository.findAllByAccountHolderName(userName);

        val otherAccessibleAccounts = powerOfAttorneyRepository.findByGranteeName(userName)
                .stream()
                .map(PowerOfAttorney::account)
                .toList();

        val merged = Stream.concat(ownAccounts.stream(), otherAccessibleAccounts.stream())
                .collect(Collectors.toMap(
                        Account::accountNumber,
                        a -> a,
                        (a1, a2) -> a1
                ))
                .values()
                .stream()
                .sorted(Comparator.comparing(Account::accountNumber))
                .toList();

        log.debug("Accessible accounts for {}: {}", userName, merged.size());
        return merged;
    }
}
