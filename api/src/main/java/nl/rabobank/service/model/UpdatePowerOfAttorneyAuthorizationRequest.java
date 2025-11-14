package nl.rabobank.service.model;

import nl.rabobank.authorizations.Authorization;

public record UpdatePowerOfAttorneyAuthorizationRequest(String paoId, Authorization authorization) {
}
