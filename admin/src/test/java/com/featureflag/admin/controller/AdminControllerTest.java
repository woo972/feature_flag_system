package com.featureflag.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.featureflag.core.service.FeatureFlagService;
import com.featureflag.core.service.RegisterFeatureFlagRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
class AdminControllerTest {
    @MockBean
    private FeatureFlagService featureFlagService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("returns 201 if flag is registered")
    @Test
    public void returns201IfFlagIsRegistered() throws Exception {
        doNothing().when(featureFlagService).register(any(RegisterFeatureFlagRequest.class));

        RegisterFeatureFlagRequest request = new RegisterFeatureFlagRequest(
                "feature-1",
                "desc",
                new HashMap<>() {{
                    put("key", "value");
                }});

        mockMvc.perform(post("/api/v1/admin/feature-flags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
