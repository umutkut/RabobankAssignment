package nl.rabobank.controller;

import lombok.RequiredArgsConstructor;
import nl.rabobank.controller.model.PowerOfAttorneyAPIResponse;
import nl.rabobank.service.CreatePowerOfAttorneyService;
import nl.rabobank.service.model.CreatePowerOfAttorneyServiceRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/power-of-attorney")
@RequiredArgsConstructor
public class PowerOfAttorneyController {

    private final CreatePowerOfAttorneyService createPowerOfAttorneyService;

    @PostMapping
    public ResponseEntity<PowerOfAttorneyAPIResponse> create(@RequestBody CreatePowerOfAttorneyServiceRequest request) {
        var poa = createPowerOfAttorneyService.create(request);
        URI location = URI.create("/api/v1/power-of-attorney/" + poa.getId());
        return ResponseEntity.created(location).body(PowerOfAttorneyAPIResponse.from(poa));
    }
}
