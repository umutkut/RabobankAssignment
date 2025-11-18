package nl.rabobank.controller;

import lombok.val;
import nl.rabobank.EmbeddedMongoTestConfiguration;
import nl.rabobank.audit.AuditLog;
import nl.rabobank.controller.advice.GlobalControllerAdvice;
import nl.rabobank.service.GetAccountAuditLogsService;
import nl.rabobank.service.GetActorAuditLogsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static nl.rabobank.TestUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuditController.class)
@Import({GlobalControllerAdvice.class, EmbeddedMongoTestConfiguration.class})
@ActiveProfiles("test")
class AuditControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    GetActorAuditLogsService getActorAuditLogsService;

    @MockitoBean
    GetAccountAuditLogsService getAccountAuditLogsService;

    @Test
    void listByActor_success() throws Exception {
        val poa = givenPowerOfAttorney();
        val log = AuditLog.created("audit-id", CREATED_AT, poa);

        Page<AuditLog> page = new PageImpl<>(List.of(log), PageRequest.of(0, 1), 1);
        when(getActorAuditLogsService.listByActor(eq("Alice"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/audits/actor/{actorName}", "Alice")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readStringFromFile("controller/audit_by_actor.json"), JsonCompareMode.STRICT));
    }

    @Test
    void listByAccount_success() throws Exception {
        val poa = givenPowerOfAttorney();
        val log = AuditLog.created("audit-id", CREATED_AT, poa);

        Page<AuditLog> page = new PageImpl<>(List.of(log), PageRequest.of(0, 1), 1);
        when(getAccountAuditLogsService.listByAccount(eq(ACCOUNT_NUMBER), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/audits/account/{accountNumber}", ACCOUNT_NUMBER)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readStringFromFile("controller/audit_by_account.json"), JsonCompareMode.STRICT));
    }
}
