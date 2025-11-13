package nl.rabobank.service.model;

import nl.rabobank.authorizations.Authorization;

public record CreatePowerOfAttorneyServiceRequest(String grantorName,
                                                  String granteeName,
                                                  String accountNumber,
                                                  Authorization authorization) {
}
