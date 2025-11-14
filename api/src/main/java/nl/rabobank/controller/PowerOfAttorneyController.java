package nl.rabobank.controller;

import lombok.RequiredArgsConstructor;
import nl.rabobank.controller.model.PowerOfAttorneyAPIResponse;
import nl.rabobank.service.CreatePowerOfAttorneyService;
import nl.rabobank.service.GetPowerOfAttorneyService;
import nl.rabobank.service.model.CreatePowerOfAttorneyServiceRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/power-of-attorney")
@RequiredArgsConstructor
public class PowerOfAttorneyController {

    private final CreatePowerOfAttorneyService createPowerOfAttorneyService;
    private final GetPowerOfAttorneyService getPowerOfAttorneyService;

    @PostMapping
    public ResponseEntity<PowerOfAttorneyAPIResponse> create(@RequestBody CreatePowerOfAttorneyServiceRequest request) {
        var poa = createPowerOfAttorneyService.create(request);
        URI location = URI.create("/api/v1/power-of-attorney/" + poa.getId());
        return ResponseEntity.created(location).body(PowerOfAttorneyAPIResponse.from(poa));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PowerOfAttorneyAPIResponse> getById(@PathVariable("id") String id) {
        var poa = getPowerOfAttorneyService.getById(id);
        return ResponseEntity.ok(PowerOfAttorneyAPIResponse.from(poa));
    }
}
