package com.example.demo.students;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end coverage of registration, login and the JWT-protected /account
 * route, running against an in-memory H2 database (see
 * application-test.properties).
 */
@SpringBootTest
@AutoConfigureMockMvc 
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void registerLoginAndFetchAccount() throws Exception {
        String email = "aluno" + System.nanoTime() + "@example.com";

        // 1) Register
        String registerBody = objectMapper.writeValueAsString(
                new StudentRegisterRequest("Joao Pereira", email, "senhaForte123"));

        mockMvc.perform(post("/students/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.password").doesNotExist());

        // 2) Duplicate registration is rejected
        mockMvc.perform(post("/students/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isConflict());

        // 3) Login
        String loginBody = objectMapper.writeValueAsString(new LoginRequest(email, "senhaForte123"));
        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn().getResponse().getContentAsString();

        String token = JsonPath.read(loginResponse, "$.token");

        // 4) /account without a token is rejected
        mockMvc.perform(get("/account"))
                .andExpect(status().is4xxClientError());

        // 5) /account with a valid token succeeds and never leaks the password
        mockMvc.perform(get("/account").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void loginWithWrongPasswordIsRejected() throws Exception {
        String email = "aluno" + System.nanoTime() + "@example.com";
        String registerBody = objectMapper.writeValueAsString(
                new StudentRegisterRequest("Maria Lima", email, "senhaCorreta1"));
        mockMvc.perform(post("/students/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated());

        String loginBody = objectMapper.writeValueAsString(new LoginRequest(email, "senha-errada"));
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isUnauthorized());
    }
}
