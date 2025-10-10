package com.featureflag.core.controller;

import com.featureflag.core.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Map;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FeatureFlagController.class)
class FeatureFlagControllerTest {
    @MockBean
    private FeatureFlagQueryService featureFlagQueryService;
    @MockBean
    private FeatureFlagCommandService featureFlagCommandService;
    @MockBean
    private FeatureFlagStreamProvider featureFlagStreamProvider;
    @Autowired
    private MockMvc mockMvc;

    @DisplayName("returns 200 if flagId is valid")
    @Test
    public void evaluate() throws Exception {
        long flagId = 1L;
        Map<String, String> criteria = null;
        when(featureFlagQueryService.evaluate(flagId, criteria)).thenReturn(true);

        mockMvc.perform(get("/api/v1/feature-flags/evaluate/" + flagId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @DisplayName("returns 400 if flagId is null")
    @Test
    public void invalidFlagId() throws Exception {
        mockMvc.perform(get("/api/v1/feature-flags/evaluate/"+null))
                .andExpect(status().isBadRequest());
    }
}