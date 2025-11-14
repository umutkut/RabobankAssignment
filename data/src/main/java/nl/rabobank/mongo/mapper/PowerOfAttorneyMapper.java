package nl.rabobank.mongo.mapper;

import nl.rabobank.account.Account;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.mongo.documents.poa.AuthorizationType;
import nl.rabobank.mongo.documents.poa.PowerOfAttorneyDocument;

public class PowerOfAttorneyMapper {
    PowerOfAttorneyMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static PowerOfAttorneyDocument toDocument(PowerOfAttorney powerOfAttorney) {
        return PowerOfAttorneyDocument.builder()
                .id(powerOfAttorney.id())
                .grantorName(powerOfAttorney.grantorName())
                .granteeName(powerOfAttorney.granteeName())
                .accountNumber(powerOfAttorney.account().getAccountNumber())
                .authorizationType(mapToAuthorizationType(powerOfAttorney.authorization()))
                .createdAt(powerOfAttorney.createdAt())
                .updatedAt(powerOfAttorney.updatedAt())
                .build();
    }

    public static PowerOfAttorney toDomain(PowerOfAttorneyDocument document, Account account) {
        return PowerOfAttorney.builder()
                .id(document.getId())
                .grantorName(document.getGrantorName())
                .granteeName(document.getGranteeName())
                .account(account)
                .authorization(mapToAuthorization(document.getAuthorizationType()))
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }

    private static AuthorizationType mapToAuthorizationType(Authorization authorization) {
        return switch (authorization) {
            case READ -> AuthorizationType.READ;
            case WRITE -> AuthorizationType.WRITE;
        };
    }

    private static Authorization mapToAuthorization(AuthorizationType authorizationType) {
        return switch (authorizationType) {
            case READ -> Authorization.READ;
            case WRITE -> Authorization.WRITE;
        };
    }
}

