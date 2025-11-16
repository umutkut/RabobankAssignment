package nl.rabobank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.repository.AccountRepository;
import nl.rabobank.repository.PowerOfAttorneyRepository;
import nl.rabobank.service.model.AccountWithAuthorization;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetAccountsAccessibleByUserService {

    private final PowerOfAttorneyRepository powerOfAttorneyRepository;
    private final AccountRepository accountRepository;

    public List<AccountWithAuthorization> listAccountsAccessibleByUser(String userName) {
        log.debug("Listing accessible accounts for user: {}", userName);

        val ownAccounts = accountRepository.findAllByAccountHolderName(userName)
                .stream()
                .map(acc -> new AccountWithAuthorization(acc, Authorization.WRITE));

        val delegatedAccounts = powerOfAttorneyRepository.findByGranteeName(userName)
                .stream()
                .map(poa -> new AccountWithAuthorization(poa.account(), poa.authorization()));

        val combined = Stream.concat(ownAccounts, delegatedAccounts)
                .sorted(Comparator.comparing(aa -> aa.account().accountNumber()))
                .toList();

        log.debug("Accessible accounts for {}: {}", userName, combined.size());
        return combined;
    }
}
