package org.example.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthFlowE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final AtomicInteger SEQ = new AtomicInteger();

    private String json(Map<String, Object> body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }

    private String login(String username, String password) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        MvcResult res = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON).content(json(body)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(res.getResponse().getContentAsString()).get("token").asText();
    }

    private String adminToken() throws Exception {
        return login("admin1", "admin123");
    }

    private String[] createGuide() throws Exception {
        String username = "guide" + SEQ.incrementAndGet();
        String password = "guidePass123";
        Map<String, Object> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        body.put("email", username + "@test.com");
        mockMvc.perform(post("/api/guides")
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON).content(json(body)))
                .andExpect(status().isOk());
        return new String[]{username, password};
    }

    private String guideToken() throws Exception {
        String[] cred = createGuide();
        return login(cred[0], cred[1]);
    }

    @Test
    @DisplayName("E2E-01 Login with valid admin -> 200, token + role=ADMIN")
    void e2e01_login_admin_returnsTokenAndRole() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("username", "admin1");
        body.put("password", "admin123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON).content(json(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value("admin1"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @DisplayName("E2E-02 Login with wrong password -> 4xx")
    void e2e02_login_wrongPassword_isRejected() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("username", "admin1");
        body.put("password", "wrong-pass");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON).content(json(body)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("E2E-03 Login with unknown user -> 4xx")
    void e2e03_login_unknownUser_isRejected() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("username", "no-such-user");
        body.put("password", "x");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON).content(json(body)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("E2E-04 Protected endpoint without token -> 4xx")
    void e2e04_protectedEndpoint_withoutToken_isRejected() throws Exception {
        mockMvc.perform(get("/api/guides"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("E2E-05 Protected endpoint with invalid token -> 4xx")
    void e2e05_protectedEndpoint_withGarbageToken_isRejected() throws Exception {
        mockMvc.perform(get("/api/guides")
                        .header("Authorization", "Bearer not.a.valid.token"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("E2E-06 Guide list with valid admin token -> 200")
    void e2e06_getGuides_withAdminToken_returnsOk() throws Exception {
        mockMvc.perform(get("/api/guides")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("E2E-07 ADMIN can view user list -> 200")
    void e2e07_getUsers_withAdminToken_returnsOk() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("E2E-08 ADMIN creates a new guide -> 200")
    void e2e08_createGuide_withAdminToken_returnsOk() throws Exception {
        String username = "guideCreate" + SEQ.incrementAndGet();
        Map<String, Object> body = new HashMap<>();
        body.put("username", username);
        body.put("password", "pass12345");
        body.put("email", username + "@test.com");
        mockMvc.perform(post("/api/guides")
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON).content(json(body)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("E2E-09 Newly created guide can log in -> 200, role=GUIDE")
    void e2e09_createdGuide_canLogin_withGuideRole() throws Exception {
        String[] cred = createGuide();
        Map<String, Object> body = new HashMap<>();
        body.put("username", cred[0]);
        body.put("password", cred[1]);
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON).content(json(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("GUIDE"));
    }

    @Test
    @DisplayName("E2E-10 GUIDE cannot access user management -> 403")
    void e2e10_getUsers_withGuideToken_isForbidden() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + guideToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("E2E-11 GUIDE cannot create a vehicle -> 403")
    void e2e11_createVehicle_withGuideToken_isForbidden() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("brand", "Mercedes");
        body.put("model", "Sprinter");
        body.put("plateNumber", "34ABC34");
        body.put("seatCapacity", 16);
        mockMvc.perform(post("/api/vehicles")
                        .header("Authorization", "Bearer " + guideToken())
                        .contentType(MediaType.APPLICATION_JSON).content(json(body)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("E2E-12 GUIDE can view vehicles -> 200")
    void e2e12_getVehicles_withGuideToken_returnsOk() throws Exception {
        mockMvc.perform(get("/api/vehicles")
                        .header("Authorization", "Bearer " + guideToken()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("E2E-13 Request for non-existing tour -> 404")
    void e2e13_getNonExistingTour_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/tours/999999")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("E2E-14 Create customer with blank fullName -> 400")
    void e2e14_createCustomer_withBlankName_returnsBadRequest() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("fullName", "");
        mockMvc.perform(post("/api/tours/1/customers")
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON).content(json(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("E2E-15 GUIDE can view own profile -> 200")
    void e2e15_getMyProfile_withGuideToken_returnsOk() throws Exception {
        mockMvc.perform(get("/api/guides/me")
                        .header("Authorization", "Bearer " + guideToken()))
                .andExpect(status().isOk());
    }
}
