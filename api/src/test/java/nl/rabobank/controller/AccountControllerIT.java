package nl.rabobank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.repository.AccountRepository;
import nl.rabobank.repository.PowerOfAttorneyRepository;
import nl.rabobank.service.IdGenerator;
import nl.rabobank.service.model.CreatePowerOfAttorneyServiceRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static nl.rabobank.TestUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerIT {

    private static final String ACCOUNT_API_PATH = "/api/v1/account";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AccountRepository accountRepository;

    @MockitoBean
    PowerOfAttorneyRepository powerOfAttorneyRepository;

    @MockitoBean
    IdGenerator idGenerator;


    @Test
    void create_success() throws Exception {
        //Given
        val account = givenPaymentAccount();
        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(account));
        when(idGenerator.generateUUID()).thenReturn(POA_ID);
        when(powerOfAttorneyRepository.save(any(PowerOfAttorney.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        val request = new CreatePowerOfAttorneyServiceRequest(GRANTOR, GRANTEE, ACCOUNT_NUMBER, Authorization.READ);

        val expectedJson = readStringFromFile("controller/create_success.json");

        //When and Then
        mockMvc.perform(post(ACCOUNT_API_PATH + "/authorization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", ACCOUNT_API_PATH + "/authorization/" + POA_ID))
                .andExpect(content().json(expectedJson, JsonCompareMode.STRICT));
    }

    @Test
    void create_accountNotFound() throws Exception {
        //Given
        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.empty());

        val request = new CreatePowerOfAttorneyServiceRequest(GRANTOR, GRANTEE, ACCOUNT_NUMBER, Authorization.READ);

        val expectedJson = readStringFromFile("controller/account_not_found.json");

        //When and Then
        mockMvc.perform(post(ACCOUNT_API_PATH + "/authorization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().json(expectedJson, JsonCompareMode.STRICT));
    }

    @Test
    void create_unsupportedOperation() throws Exception {
        //Given
        var account = givenPaymentAccount();
        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(account));

        val request = new CreatePowerOfAttorneyServiceRequest("Some other grantor", GRANTEE, ACCOUNT_NUMBER, Authorization.READ);

        val expectedJson = readStringFromFile("controller/unsupported_operation.json");

        //When and Then
        mockMvc.perform(post(ACCOUNT_API_PATH + "/authorization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(content().json(expectedJson, JsonCompareMode.STRICT));
    }

    @Test
    void listByGrantee_success_returnsAccounts() throws Exception {
        // Given
        val ownAccount = givenSavingsAccount().toBuilder().accountHolderName(GRANTEE).build();
        when(accountRepository.findAllByAccountHolderName(GRANTEE)).thenReturn(List.of(ownAccount));

        val poa = givenPowerOfAttorney();
        when(powerOfAttorneyRepository.findByGranteeName(GRANTEE))
                .thenReturn(List.of(poa));

        val expectedJson = readStringFromFile("controller/accounts_list.json");

        // When & Then
        mockMvc.perform(get(ACCOUNT_API_PATH + "/accessible-by/" + GRANTEE))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson, JsonCompareMode.STRICT));
    }

    @Test
    void listByGrantor_success_returnsList() throws Exception {
        // Given
        val poa1 = givenPowerOfAttorney();
        val poa2 = givenPowerOfAttorney().toBuilder()
                .id("poa-2")
                .account(givenSavingsAccount())
                .build();
        when(powerOfAttorneyRepository.findByGrantorName(eq(GRANTOR)))
                .thenReturn(List.of(poa1, poa2));

        val expectedJson = readStringFromFile("controller/poas_list.json");

        // When and then
        mockMvc.perform(get(ACCOUNT_API_PATH + "/authorization/granted-by/" + GRANTOR))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson, JsonCompareMode.STRICT));
    }
}
