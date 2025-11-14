package nl.rabobank.controller;

import lombok.RequiredArgsConstructor;
import lombok.val;
import nl.rabobank.controller.model.PowerOfAttorneyAPIResponse;
import nl.rabobank.service.CreatePowerOfAttorneyService;
import nl.rabobank.service.GetGranteePowerOfAttorneyService;
import nl.rabobank.service.GetGrantorPowerOfAttorneyService;
import nl.rabobank.service.GetPowerOfAttorneyByIdService;
import nl.rabobank.service.model.CreatePowerOfAttorneyServiceRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/power-of-attorney")
@RequiredArgsConstructor
public class PowerOfAttorneyController {

    private final CreatePowerOfAttorneyService createPowerOfAttorneyService;
    private final GetPowerOfAttorneyByIdService getPowerOfAttorneyByIdService;
    private final GetGranteePowerOfAttorneyService getGranteePowerOfAttorneyService;
    private final GetGrantorPowerOfAttorneyService getGrantorPowerOfAttorneyService;

    @PostMapping
    public ResponseEntity<PowerOfAttorneyAPIResponse> create(@RequestBody CreatePowerOfAttorneyServiceRequest request) {
        val poa = createPowerOfAttorneyService.create(request);
        URI location = URI.create("/api/v1/power-of-attorney/" + poa.getId());
        return ResponseEntity.created(location).body(PowerOfAttorneyAPIResponse.from(poa));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PowerOfAttorneyAPIResponse> getById(@PathVariable("id") String id) {
        val poa = getPowerOfAttorneyByIdService.getById(id);
        return ResponseEntity.ok(PowerOfAttorneyAPIResponse.from(poa));
    }

    @GetMapping("/grantee/{granteeName}")
    public ResponseEntity<Page<PowerOfAttorneyAPIResponse>> listByGrantee(
            @PathVariable("granteeName") String granteeName,
            @PageableDefault(sort = "accountNumber", size = 5) Pageable pageable) {
        val poas = getGranteePowerOfAttorneyService.listPoasForUser(granteeName, pageable);
        val body = poas.map(PowerOfAttorneyAPIResponse::from);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/grantor/{grantorName}")
    public ResponseEntity<Page<PowerOfAttorneyAPIResponse>> listByGrantor(
            @PathVariable("grantorName") String grantorName,
            @PageableDefault(sort = "accountNumber", size = 5) Pageable pageable) {
        val poas = getGrantorPowerOfAttorneyService.listPoasForGrantor(grantorName, pageable);
        val body = poas.map(PowerOfAttorneyAPIResponse::from);
        return ResponseEntity.ok(body);
    }
}
