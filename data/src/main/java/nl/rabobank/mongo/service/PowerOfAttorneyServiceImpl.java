package nl.rabobank.mongo.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.mongo.documents.account.AccountDocument;
import nl.rabobank.mongo.documents.poa.PowerOfAttorneyDocument;
import nl.rabobank.mongo.mapper.AccountMapper;
import nl.rabobank.mongo.mapper.PowerOfAttorneyMapper;
import nl.rabobank.mongo.repository.AccountMongoRepository;
import nl.rabobank.mongo.repository.PowerOfAttorneyMongoRepository;
import nl.rabobank.repository.PowerOfAttorneyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PowerOfAttorneyServiceImpl implements PowerOfAttorneyService {

    private final PowerOfAttorneyMongoRepository powerOfAttorneyMongoRepository;
    private final AccountMongoRepository accountMongoRepository;

    @Override
    public PowerOfAttorney save(PowerOfAttorney powerOfAttorney) {
        val document = PowerOfAttorneyMapper.toDocument(powerOfAttorney);
        val savedDocument = powerOfAttorneyMongoRepository.save(document);
        return PowerOfAttorneyMapper.toDomain(savedDocument, powerOfAttorney.account());
    }

    @Override
    public Optional<PowerOfAttorney> findById(String id) {
        val optPoaDocument = powerOfAttorneyMongoRepository.findById(id);
        return mapOptionalPoaDocToDomain(optPoaDocument);
    }

    @Override
    public List<PowerOfAttorney> findByGranteeName(String granteeName) {
        val docList = powerOfAttorneyMongoRepository.findByGranteeNameIgnoreCase(granteeName);
        return mapListOfPoaDocsToDomain(docList);
    }

    @Override
    public List<PowerOfAttorney> findByGrantorName(String grantorName) {
        val docs = powerOfAttorneyMongoRepository.findByGrantorNameIgnoreCase(grantorName);
        return mapListOfPoaDocsToDomain(docs);
    }

    @Override
    public Optional<PowerOfAttorney> findByGrantorAndGranteeAndAccountNumber(String grantor, String grantee, String accountNumber) {
        val paoDocument = powerOfAttorneyMongoRepository.findByGrantorNameIgnoreCaseAndGranteeNameIgnoreCaseAndAccountNumber(grantor, grantee, accountNumber);
        return mapOptionalPoaDocToDomain(paoDocument);
    }

    @Override
    public void deleteById(String id) {
        powerOfAttorneyMongoRepository.deleteById(id);
    }

    private List<PowerOfAttorney> mapListOfPoaDocsToDomain(List<PowerOfAttorneyDocument> poaDocuments) {
        val documentNumbers = poaDocuments.stream().map(PowerOfAttorneyDocument::getAccountNumber).toList();
        val accountNumberAccountMap = accountMongoRepository.findAllByAccountNumberIn(documentNumbers).stream().collect(
                java.util.stream.Collectors.toMap(
                        AccountDocument::getAccountNumber,
                        AccountMapper::toDomain
                )
        );
        return poaDocuments.stream().map(document -> {
            val account = accountNumberAccountMap.get(document.getAccountNumber());
            return PowerOfAttorneyMapper.toDomain(document, account);
        }).toList();
    }

    private Optional<PowerOfAttorney> mapOptionalPoaDocToDomain(Optional<PowerOfAttorneyDocument> optPoaDocument) {
        if (optPoaDocument.isEmpty()) {
            return Optional.empty();
        }
        val poaDocument = optPoaDocument.get();

        val optAccountDoc = accountMongoRepository.findById(poaDocument.getAccountNumber());
        return optAccountDoc.map(accountDoc -> PowerOfAttorneyMapper.toDomain(
                poaDocument,
                AccountMapper.toDomain(accountDoc)
        ));
    }
}

