package nl.rabobank.controller;

import lombok.RequiredArgsConstructor;
import lombok.val;
import nl.rabobank.controller.model.AuditLogAPIResponse;
import nl.rabobank.service.GetAccountAuditLogsService;
import nl.rabobank.service.GetActorAuditLogsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audits")
@RequiredArgsConstructor
public class AuditController {

    private final GetActorAuditLogsService getActorAuditLogsService;
    private final GetAccountAuditLogsService getAccountAuditLogsService;

    @GetMapping("/actor/{actorName}")
    public ResponseEntity<Page<AuditLogAPIResponse>> listByActor(
            @PathVariable("actorName") String actorName,
            @PageableDefault(sort = "createdAt", size = 5) Pageable pageable) {
        val logs = getActorAuditLogsService.listByActor(actorName, pageable);
        val body = logs.map(AuditLogAPIResponse::from);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<Page<AuditLogAPIResponse>> listByAccount(
            @PathVariable("accountNumber") String accountNumber,
            @PageableDefault(sort = "createdAt", size = 5) Pageable pageable) {
        val logs = getAccountAuditLogsService.listByAccount(accountNumber, pageable);
        val body = logs.map(AuditLogAPIResponse::from);
        return ResponseEntity.ok(body);
    }
}
