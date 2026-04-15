package com.reflect.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reflect.controller.dto.CheckInRequest;
import com.reflect.controller.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CheckInControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        String unique = "checkin-" + System.nanoTime() + "@example.com";
        var registerReq = new RegisterRequest(unique, "password123", "Test User");
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        accessToken = body.get("accessToken").asText();
    }

    @Test
    void getCurrent_returns404WhenNoCheckIn() throws Exception {
        mockMvc.perform(get("/api/check-ins/current")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_returns201() throws Exception {
        var request = new CheckInRequest("Shipped feature X", null, null, null, null, null);
        mockMvc.perform(post("/api/check-ins")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.wins").value("Shipped feature X"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void update_savesProgress() throws Exception {
        var createReq = new CheckInRequest("Wins", null, null, null, null, null);
        MvcResult createResult = mockMvc.perform(post("/api/check-ins")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andReturn();
        JsonNode created = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String checkInId = created.get("id").asText();

        var updateReq = new CheckInRequest(null, "Felt stuck on CI", (short) 6, null, null, null);
        mockMvc.perform(put("/api/check-ins/" + checkInId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wins").value("Wins"))
                .andExpect(jsonPath("$.friction").value("Felt stuck on CI"))
                .andExpect(jsonPath("$.energyRating").value(6));
    }

    @Test
    void list_returnsPaginatedResults() throws Exception {
        mockMvc.perform(get("/api/check-ins")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void endpoints_return401WithoutToken() throws Exception {
        mockMvc.perform(get("/api/check-ins/current"))
                .andExpect(status().isUnauthorized());
    }
}
