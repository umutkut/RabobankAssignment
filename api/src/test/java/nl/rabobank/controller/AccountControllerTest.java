package nl.rabobank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import nl.rabobank.EmbeddedMongoTestConfiguration;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.controller.advice.GlobalControllerAdvice;
import nl.rabobank.exception.AccountNotFoundException;
import nl.rabobank.exception.ForbiddenOperationException;
import nl.rabobank.exception.PowerOfAttorneyAlreadyExistException;
import nl.rabobank.exception.PowerOfAttorneyNotFoundException;
import nl.rabobank.service.*;
import nl.rabobank.service.model.AccountWithAuthorization;
import nl.rabobank.service.model.CreatePowerOfAttorneyServiceRequest;
import nl.rabobank.service.model.UpdatePowerOfAttorneyAuthorizationRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static nl.rabobank.TestUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AccountController.class)
@Import({GlobalControllerAdvice.class, EmbeddedMongoTestConfiguration.class})
@ActiveProfiles("test")
class AccountControllerTest {

    private static final String ACCOUNT_API_PATH = "/api/v1/account";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CreatePowerOfAttorneyService createPowerOfAttorneyService;

    @MockitoBean
    GetPowerOfAttorneyByIdService getPowerOfAttorneyByIdService;

    @MockitoBean
    GetAccountsAccessibleByUserService getGranteePowerOfAttorneyService;

    @MockitoBean
    GetGrantorPowerOfAttorneyService getGrantorPowerOfAttorneyService;

    @MockitoBean
    UpdatePowerOfAttorneyAuthorizationService updatePowerOfAttorneyAuthorizationService;

    @MockitoBean
    DeletePowerOfAttorneyService deletePowerOfAttorneyService;

    @Nested
    class CreateAuthorizationAccountControllerTest {

        @Test
        void create_success() throws Exception {
            //Given
            val poa = givenPowerOfAttorney();
            when(createPowerOfAttorneyService.create(any(CreatePowerOfAttorneyServiceRequest.class)))
                    .thenReturn(poa);

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
            when(createPowerOfAttorneyService.create(any(CreatePowerOfAttorneyServiceRequest.class)))
                    .thenThrow(new AccountNotFoundException("With accountNumber: " + ACCOUNT_NUMBER));

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
            when(createPowerOfAttorneyService.create(any(CreatePowerOfAttorneyServiceRequest.class)))
                    .thenThrow(new ForbiddenOperationException("User cannot operate. Some other grantor is not owner of the requested account."));

            val request = new CreatePowerOfAttorneyServiceRequest(GRANTOR, GRANTEE, ACCOUNT_NUMBER, Authorization.READ);

            val expectedJson = readStringFromFile("controller/unsupported_operation.json");

            //When and Then
            mockMvc.perform(post(ACCOUNT_API_PATH + "/authorization")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden())
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
            mockMvc.perform(post(ACCOUNT_API_PATH + "/authorization")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(content().json(expectedJson, JsonCompareMode.STRICT));
        }
    }

    @Nested
    class GetAuthorizationAccountControllerTest {
        @Test
        void getById_success() throws Exception {
            // Given
            val poa = givenPowerOfAttorney();
            when(getPowerOfAttorneyByIdService.getById(POA_ID)).thenReturn(poa);
            val expectedJson = readStringFromFile("controller/create_success.json");

            // When & Then
            mockMvc.perform(get(ACCOUNT_API_PATH + "/authorization/" + POA_ID))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson, JsonCompareMode.STRICT));
        }

        @Test
        void getById_notFound() throws Exception {
            // Given
            when(getPowerOfAttorneyByIdService.getById(POA_ID)).thenThrow(new PowerOfAttorneyNotFoundException("With id: " + POA_ID));
            val expectedJson = readStringFromFile("controller/poa_not_found.json");

            // When & Then
            mockMvc.perform(get(ACCOUNT_API_PATH + "/authorization/" + POA_ID))
                    .andExpect(status().isNotFound())
                    .andExpect(content().json(expectedJson, JsonCompareMode.STRICT));
        }
    }

    @Nested
    class ListAuthorizationAccountControllerTest {
        @Test
        void listByGrantee_success_returnsAccounts() throws Exception {
            // Given
            val acc1 = givenPaymentAccount();
            val acc2 = givenSavingsAccount().toBuilder().accountHolderName(GRANTEE).build();
            when(getGranteePowerOfAttorneyService.listAccountsAccessibleByUser(GRANTEE))
                    .thenReturn(List.of(
                            new AccountWithAuthorization(acc1, Authorization.READ),
                            new AccountWithAuthorization(acc2, Authorization.WRITE)
                    ));

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
            when(getGrantorPowerOfAttorneyService.listPoasForGrantor(GRANTOR))
                    .thenReturn(List.of(poa1, poa2));

            val expectedJson = readStringFromFile("controller/poas_list.json");

            // When & Then
            mockMvc.perform(get(ACCOUNT_API_PATH + "/authorization/granted-by/" + GRANTOR))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson, JsonCompareMode.STRICT));
        }
    }

    @Nested
    class UpdateAuthorizationAccountControllerTest {

        @Test
        void updateAuthorization_success() throws Exception {
            // Given
            val updated = givenPowerOfAttorney().toBuilder().authorization(Authorization.WRITE).build();
            when(updatePowerOfAttorneyAuthorizationService.updateAuthorization(any(UpdatePowerOfAttorneyAuthorizationRequest.class)))
                    .thenReturn(updated);

            val expectedJson = readStringFromFile("controller/update_success.json");

            // When & Then
            mockMvc.perform(put(ACCOUNT_API_PATH + "/authorization/" + POA_ID + "?newAuthorization=WRITE"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson, JsonCompareMode.STRICT));
        }

        @Test
        void updateAuthorization_notFound() throws Exception {
            // Given
            when(updatePowerOfAttorneyAuthorizationService.updateAuthorization(any(UpdatePowerOfAttorneyAuthorizationRequest.class)))
                    .thenThrow(new PowerOfAttorneyNotFoundException("With id: " + POA_ID));

            val expectedJson = readStringFromFile("controller/poa_not_found.json");

            // When & Then
            mockMvc.perform(put(ACCOUNT_API_PATH + "/authorization/" + POA_ID + "?newAuthorization=WRITE"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().json(expectedJson, JsonCompareMode.STRICT));
        }
    }

    @Nested
    class DeleteAuthorizationAccountControllerTest {

        @Test
        void delete_success() throws Exception {
            // When & Then
            mockMvc.perform(delete(ACCOUNT_API_PATH + "/authorization/" + POA_ID + "?grantorName=" + GRANTOR))
                    .andExpect(status().isNoContent());
        }

        @Test
        void delete_notFound() throws Exception {
            // Given
            doThrow(new PowerOfAttorneyNotFoundException("With id: " + POA_ID))
                    .when(deletePowerOfAttorneyService).deleteByIdAsGrantor(POA_ID, GRANTOR);
            val expectedJson = readStringFromFile("controller/poa_not_found.json");

            // When & Then
            mockMvc.perform(delete(ACCOUNT_API_PATH + "/authorization/" + POA_ID + "?grantorName=" + GRANTOR))
                    .andExpect(status().isNotFound())
                    .andExpect(content().json(expectedJson, JsonCompareMode.STRICT));
        }

        @Test
        void delete_forbidden() throws Exception {
            // Given
            doThrow(new ForbiddenOperationException("Only the grantor may delete this PoA"))
                    .when(deletePowerOfAttorneyService).deleteByIdAsGrantor(POA_ID, GRANTOR);
            val expectedJson = readStringFromFile("controller/forbidden_operation.json");

            // When & Then
            mockMvc.perform(delete(ACCOUNT_API_PATH + "/authorization/" + POA_ID + "?grantorName=" + GRANTOR))
                    .andExpect(status().isForbidden())
                    .andExpect(content().json(expectedJson, JsonCompareMode.STRICT));
        }
    }
}
