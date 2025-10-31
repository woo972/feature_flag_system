package com.featureflag.core.featureflag.presentation;

import com.featureflag.core.featureflag.application.command.FeatureFlagCommandService;
import com.featureflag.core.featureflag.application.query.FeatureFlagQueryService;
import com.featureflag.core.featureflag.presentation.mapper.FeatureFlagResponseMapper;
import com.featureflag.core.admin.infrastructure.security.JwtUtil;
import com.featureflag.core.apikey.application.service.ApiKeyApplicationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FeatureFlagController.class)
@Import(FeatureFlagResponseMapper.class)
@AutoConfigureMockMvc(addFilters = false)
class FeatureFlagControllerTest {
    @MockBean
    private FeatureFlagQueryService featureFlagQueryService;
    @MockBean
    private FeatureFlagCommandService featureFlagCommandService;
    @MockBean
    private FeatureFlagStreamProvider featureFlagStreamProvider;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private ApiKeyApplicationService apiKeyApplicationService;
    @Autowired
    private MockMvc mockMvc;

    @DisplayName("returns 200 if flagId is valid")
    @Test
    void evaluate() throws Exception {
        long flagId = 1L;
        Map<String, String> criteria = null;
        when(featureFlagQueryService.evaluate(flagId, criteria)).thenReturn(true);

        mockMvc.perform(get("/api/v1/feature-flags/evaluate/" + flagId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @DisplayName("returns 400 if flagId is null")
    @Test
    void invalidFlagId() throws Exception {
        mockMvc.perform(get("/api/v1/feature-flags/evaluate/" + null))
                .andExpect(status().isBadRequest());
    }
}
