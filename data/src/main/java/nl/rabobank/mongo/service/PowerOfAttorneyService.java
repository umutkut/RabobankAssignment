package nl.rabobank.mongo.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.mongo.documents.account.AccountDocument;
import nl.rabobank.mongo.documents.poa.PowerOfAttorneyDocument;
import nl.rabobank.mongo.mapper.AccountMapper;
import nl.rabobank.mongo.mapper.PowerOfAttorneyMapper;
import nl.rabobank.mongo.repositories.AccountRepository;
import nl.rabobank.mongo.repositories.PowerOfAttorneyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PowerOfAttorneyService {

    private final PowerOfAttorneyRepository powerOfAttorneyRepository;
    private final AccountRepository accountRepository;

    public PowerOfAttorney save(PowerOfAttorney powerOfAttorney) {
        val document = PowerOfAttorneyMapper.toDocument(powerOfAttorney);
        val savedDocument = powerOfAttorneyRepository.save(document);
        return PowerOfAttorneyMapper.toDomain(savedDocument, powerOfAttorney.getAccount());
    }

    public Optional<PowerOfAttorney> findById(String id) {
        val optPoaDocument = powerOfAttorneyRepository.findById(id);
        if (optPoaDocument.isEmpty()) {
            return Optional.empty();
        }
        val poaDocument = optPoaDocument.get();

        val optAccountDoc = accountRepository.findById(poaDocument.getAccountNumber());
        return optAccountDoc.map(accountDoc -> PowerOfAttorneyMapper.toDomain(
                poaDocument,
                AccountMapper.toDomain(accountDoc)
        ));
    }

    public List<PowerOfAttorney> findByGranteeName(String granteeName) {
        val poaDocuments = powerOfAttorneyRepository.findByGranteeNameAndRevokedFalse(granteeName);
        return formPowerOfAttorneys(poaDocuments);
    }

    public List<PowerOfAttorney> findByGrantorName(String grantorName) {
        val poaDocuments = powerOfAttorneyRepository.findByGrantorNameAndRevokedFalse(grantorName);
        return formPowerOfAttorneys(poaDocuments);
    }

    private List<PowerOfAttorney> formPowerOfAttorneys(List<PowerOfAttorneyDocument> poaDocuments) {
        val documentNumbers = poaDocuments.stream().map(PowerOfAttorneyDocument::getAccountNumber).toList();
        val accountNumberAccountMap = accountRepository.findAllByAccountNumberIn(documentNumbers).stream().collect(
                Collectors.toMap(
                        AccountDocument::getAccountNumber,
                        AccountMapper::toDomain
                )
        );
        return poaDocuments.stream().map(document -> {
            val account = accountNumberAccountMap.get(document.getAccountNumber());
            return PowerOfAttorneyMapper.toDomain(document, account);
        }).toList();
    }
}

