package nl.rabobank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.controller.advice.GlobalControllerAdvice;
import nl.rabobank.exception.AccountNotFoundException;
import nl.rabobank.exception.PowerOfAttorneyAlreadyExistException;
import nl.rabobank.exception.PowerOfAttorneyNotFoundException;
import nl.rabobank.exception.UnsupportedUserOperationException;
import nl.rabobank.service.CreatePowerOfAttorneyService;
import nl.rabobank.service.GetAccessibleAccountsService;
import nl.rabobank.service.GetPowerOfAttorneyByIdService;
import nl.rabobank.service.model.CreatePowerOfAttorneyServiceRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static nl.rabobank.TestUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PowerOfAttorneyController.class)
@Import(GlobalControllerAdvice.class)
class PowerOfAttorneyControllerTest {

    private static final String POA_API_PATH = "/api/v1/power-of-attorney";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CreatePowerOfAttorneyService createPowerOfAttorneyService;

    @MockitoBean
    GetPowerOfAttorneyByIdService getPowerOfAttorneyByIdService;

    @MockitoBean
    GetAccessibleAccountsService getAccessibleAccountsService;

    @Test
    void create_success() throws Exception {
        //Given
        val poa = givenPowerOfAttorney();
        when(createPowerOfAttorneyService.create(any(CreatePowerOfAttorneyServiceRequest.class)))
                .thenReturn(poa);

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
        when(createPowerOfAttorneyService.create(any(CreatePowerOfAttorneyServiceRequest.class)))
                .thenThrow(new AccountNotFoundException("With accountNumber: " + ACCOUNT_NUMBER));

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
        when(createPowerOfAttorneyService.create(any(CreatePowerOfAttorneyServiceRequest.class)))
                .thenThrow(new UnsupportedUserOperationException("Some other grantor is not owner of the requested account."));

        val request = new CreatePowerOfAttorneyServiceRequest(GRANTOR, GRANTEE, ACCOUNT_NUMBER, Authorization.READ);

        val expectedJson = readStringFromFile("controller/unsupported_operation.json");

        //When and Then
        mockMvc.perform(post(POA_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedJson, JsonCompareMode.STRICT));
    }

    @Test
    void create_poaAlreadyExists() throws Exception {
        //Given
        when(createPowerOfAttorneyService.create(any(CreatePowerOfAttorneyServiceRequest.class)))
                .thenThrow(new PowerOfAttorneyAlreadyExistException());

        val request = new CreatePowerOfAttorneyServiceRequest(GRANTOR, GRANTEE, ACCOUNT_NUMBER, Authorization.READ);

        val expectedJson = readStringFromFile("controller/poa_already_exists.json");

        //When and Then
        mockMvc.perform(post(POA_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().json(expectedJson, JsonCompareMode.STRICT));
    }

    @Test
    void getById_success() throws Exception {
        // Given
        val poa = givenPowerOfAttorney();
        when(getPowerOfAttorneyByIdService.getById(POA_ID)).thenReturn(poa);
        val expectedJson = readStringFromFile("controller/create_success.json");

        // When & Then
        mockMvc.perform(get(POA_API_PATH + "/" + POA_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson, JsonCompareMode.STRICT));
    }

    @Test
    void getById_notFound() throws Exception {
        // Given
        when(getPowerOfAttorneyByIdService.getById(POA_ID)).thenThrow(new PowerOfAttorneyNotFoundException("With id: " + POA_ID));
        val expectedJson = readStringFromFile("controller/poa_not_found.json");

        // When & Then
        mockMvc.perform(get(POA_API_PATH + "/" + POA_ID))
                .andExpect(status().isNotFound())
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
        when(getAccessibleAccountsService.listPoasForUser(eq(GRANTEE), any(Pageable.class)))
                .thenReturn(page);

        val expectedJson = readStringFromFile("controller/poas_by_grantee_page0_size2.json");

        // When & Then
        mockMvc.perform(get(POA_API_PATH + "/grantee/" + GRANTEE + "?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson, JsonCompareMode.STRICT));
    }
}
