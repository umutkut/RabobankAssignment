package nl.rabobank.mongo.repository;

import lombok.RequiredArgsConstructor;
import lombok.val;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.mongo.client.AccountMongoClient;
import nl.rabobank.mongo.client.PowerOfAttorneyMongoClient;
import nl.rabobank.mongo.documents.account.AccountDocument;
import nl.rabobank.mongo.documents.poa.PowerOfAttorneyDocument;
import nl.rabobank.mongo.mapper.AccountMapper;
import nl.rabobank.mongo.mapper.PowerOfAttorneyMapper;
import nl.rabobank.repository.PowerOfAttorneyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PowerOfAttorneyRepositoryImpl implements PowerOfAttorneyRepository {

    private final PowerOfAttorneyMongoClient powerOfAttorneyMongoClient;
    private final AccountMongoClient accountMongoClient;

    @Override
    public PowerOfAttorney save(PowerOfAttorney powerOfAttorney) {
        val document = PowerOfAttorneyMapper.toDocument(powerOfAttorney);
        val savedDocument = powerOfAttorneyMongoClient.save(document);
        return PowerOfAttorneyMapper.toDomain(savedDocument, powerOfAttorney.account());
    }

    @Override
    public Optional<PowerOfAttorney> findById(String id) {
        val optPoaDocument = powerOfAttorneyMongoClient.findById(id);
        return mapOptionalPoaDocToDomain(optPoaDocument);
    }

    @Override
    public Page<PowerOfAttorney> findByGranteeName(String granteeName, Pageable pageable) {
        val page = powerOfAttorneyMongoClient.findByGranteeName(granteeName, pageable);
        val content = mapListOfPoaDocsToDomain(page.getContent());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    public Page<PowerOfAttorney> findByGrantorName(String grantorName, Pageable pageable) {
        val page = powerOfAttorneyMongoClient.findByGrantorName(grantorName, pageable);
        val content = mapListOfPoaDocsToDomain(page.getContent());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    public Optional<PowerOfAttorney> findByGrantorAndGranteeAndAccountNumber(String grantor, String grantee, String accountNumber) {
        val paoDocument = powerOfAttorneyMongoClient.findByGrantorNameAndGranteeNameAndAccountNumber(grantor, grantee, accountNumber);
        return mapOptionalPoaDocToDomain(paoDocument);
    }

    @Override
    public void deleteById(String id) {
        powerOfAttorneyMongoClient.deleteById(id);
    }

    private List<PowerOfAttorney> mapListOfPoaDocsToDomain(List<PowerOfAttorneyDocument> poaDocuments) {
        val documentNumbers = poaDocuments.stream().map(PowerOfAttorneyDocument::getAccountNumber).toList();
        val accountNumberAccountMap = accountMongoClient.findAllByAccountNumberIn(documentNumbers).stream().collect(
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

    private Optional<PowerOfAttorney> mapOptionalPoaDocToDomain(Optional<PowerOfAttorneyDocument> optPoaDocument) {
        if (optPoaDocument.isEmpty()) {
            return Optional.empty();
        }
        val poaDocument = optPoaDocument.get();

        val optAccountDoc = accountMongoClient.findById(poaDocument.getAccountNumber());
        return optAccountDoc.map(accountDoc -> PowerOfAttorneyMapper.toDomain(
                poaDocument,
                AccountMapper.toDomain(accountDoc)
        ));
    }
}

