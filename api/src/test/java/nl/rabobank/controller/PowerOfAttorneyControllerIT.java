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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
class PowerOfAttorneyControllerIT {

    private static final String POA_API_PATH = "/api/v1/power-of-attorney";

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
        mockMvc.perform(post(POA_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", POA_API_PATH + "/" + POA_ID))
                .andExpect(content().json(expectedJson, JsonCompareMode.STRICT));
    }

    @Test
    void create_accountNotFound() throws Exception {
        //Given
        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.empty());

        val request = new CreatePowerOfAttorneyServiceRequest(GRANTOR, GRANTEE, ACCOUNT_NUMBER, Authorization.READ);

        val expectedJson = readStringFromFile("controller/account_not_found.json");

        //When and Then
        mockMvc.perform(post(POA_API_PATH)
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
        mockMvc.perform(post(POA_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedJson, JsonCompareMode.STRICT));
    }

    @Test
    void listByGrantee_success_withPagination() throws Exception {
        // Given
        val poa1 = givenPowerOfAttorney();
        val poa2 = givenPowerOfAttorney().toBuilder()
                .id("poa-2")
                .account(givenSavingsAccount())
                .build();
        val pageable = PageRequest.of(0, 2);
        val page = new PageImpl<>(List.of(poa1, poa2), pageable, 2);
        when(powerOfAttorneyRepository.findByGranteeName(eq(GRANTEE), any(Pageable.class)))
                .thenReturn(page);

        val expectedJson = readStringFromFile("controller/poas_paginated.json");

        // When & Then
        mockMvc.perform(get(POA_API_PATH + "/grantee/" + GRANTEE + "?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson, JsonCompareMode.STRICT));
    }

    @Test
    void listByGrantor_success_withPagination() throws Exception {
        // Given
        val poa1 = givenPowerOfAttorney();
        val poa2 = givenPowerOfAttorney().toBuilder()
                .id("poa-2")
                .account(givenSavingsAccount())
                .build();
        val pageable = PageRequest.of(0, 2);
        val page = new PageImpl<>(List.of(poa1, poa2), pageable, 2);
        when(powerOfAttorneyRepository.findByGrantorName(eq(GRANTOR), any(Pageable.class)))
                .thenReturn(page);

        val expectedJson = readStringFromFile("controller/poas_paginated.json");

        // When and then
        mockMvc.perform(get(POA_API_PATH + "/grantor/" + GRANTOR + "?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson, JsonCompareMode.STRICT));
    }
}
