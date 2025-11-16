package nl.rabobank.controller;

import lombok.RequiredArgsConstructor;
import lombok.val;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.controller.model.AccountWithAuthorizationAPIResponse;
import nl.rabobank.controller.model.PowerOfAttorneyAPIResponse;
import nl.rabobank.service.*;
import nl.rabobank.service.model.CreatePowerOfAttorneyServiceRequest;
import nl.rabobank.service.model.UpdatePowerOfAttorneyAuthorizationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final CreatePowerOfAttorneyService createPowerOfAttorneyService;
    private final GetPowerOfAttorneyByIdService getPowerOfAttorneyByIdService;
    private final GetAccountsAccessibleByUserService getGranteePowerOfAttorneyService;
    private final GetGrantorPowerOfAttorneyService getGrantorPowerOfAttorneyService;
    private final UpdatePowerOfAttorneyAuthorizationService updatePowerOfAttorneyAuthorizationService;
    private final DeletePowerOfAttorneyService deletePowerOfAttorneyService;


    @PostMapping("/authorization")
    public ResponseEntity<PowerOfAttorneyAPIResponse> create(@RequestBody CreatePowerOfAttorneyServiceRequest request) {
        val poa = createPowerOfAttorneyService.create(request);
        URI location = URI.create("/api/v1/account/authorization/" + poa.id());
        return ResponseEntity.created(location).body(PowerOfAttorneyAPIResponse.from(poa));
    }

    @GetMapping("/authorization/{id}")
    public ResponseEntity<PowerOfAttorneyAPIResponse> getById(@PathVariable("id") String id) {
        val poa = getPowerOfAttorneyByIdService.getById(id);
        return ResponseEntity.ok(PowerOfAttorneyAPIResponse.from(poa));
    }

    @PutMapping("/authorization/{id}")
    public ResponseEntity<PowerOfAttorneyAPIResponse> updateAuthorization(@PathVariable("id") String id,
                                                                          @RequestParam("newAuthorization") String newAuthorization) {
        val poa = updatePowerOfAttorneyAuthorizationService.updateAuthorization(new UpdatePowerOfAttorneyAuthorizationRequest(id, Authorization.valueOf(newAuthorization)));
        return ResponseEntity.ok(PowerOfAttorneyAPIResponse.from(poa));
    }

    @GetMapping("/accessible-by/{granteeName}")
    public ResponseEntity<List<AccountWithAuthorizationAPIResponse>> listByGrantee(
            @PathVariable("granteeName") String granteeName) {
        val accesses = getGranteePowerOfAttorneyService.listAccountsAccessibleByUser(granteeName);
        val body = accesses.stream().map(AccountWithAuthorizationAPIResponse::from).toList();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/authorization/granted-by/{grantorName}")
    public ResponseEntity<List<PowerOfAttorneyAPIResponse>> listByGrantor(
            @PathVariable("grantorName") String grantorName) {
        val poas = getGrantorPowerOfAttorneyService.listPoasForGrantor(grantorName);
        val body = poas.stream().map(PowerOfAttorneyAPIResponse::from).toList();
        return ResponseEntity.ok(body);
    }

    @DeleteMapping(value = "/authorization/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id,
                                       @RequestParam("grantorName") String grantorName) {
        deletePowerOfAttorneyService.deleteByIdAsGrantor(id, grantorName);
        return ResponseEntity.noContent().build();
    }
}
