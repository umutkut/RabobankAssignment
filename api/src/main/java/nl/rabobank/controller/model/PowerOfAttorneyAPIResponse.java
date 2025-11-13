package nl.rabobank.controller.model;

import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;


public record PowerOfAttorneyAPIResponse(
        String id,
        String grantorName,
        String granteeName,
        AccountAPIResponse account,
        Authorization authorization
) {
    public static PowerOfAttorneyAPIResponse from(PowerOfAttorney poa) {
        return new PowerOfAttorneyAPIResponse(
                poa.getId(),
                poa.getGrantorName(),
                poa.getGranteeName(),
                AccountAPIResponse.from(poa.getAccount()),
                poa.getAuthorization()
        );
    }
}
