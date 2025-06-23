package com.featureflag.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.featureflag.admin.service.AdminFeatureFlagService;
import com.featureflag.core.service.RegisterFeatureFlagRequest;
import com.featureflag.shared.model.FeatureFlag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.HashMap;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerTest {
    @MockBean
    private AdminFeatureFlagService adminFeatureFlagService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("returns 201 if flag is registered")
    @Test
    public void returns201IfFlagIsRegistered() throws Exception {
        doNothing().when(adminFeatureFlagService).register(any(RegisterFeatureFlagRequest.class));

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

    @DisplayName("returns feature-flag model ")
    @Test
    public void returnsFeatureFlagModel() throws Exception {
        Long id = 1L;
        FeatureFlag featureFlag = new FeatureFlag();
        featureFlag.setId(id);
        when(adminFeatureFlagService.get(id)).thenReturn(featureFlag);

        mockMvc.perform(get("/api/v1/admin/feature-flags/"+id))
                .andExpect(model().attribute("featureFlag", featureFlag))
                .andExpect(view().name("/admin/feature-flag-detail"));
    }

    @DisplayName("returns feature-flags page model")
    @Test
    public void returnsFeatureFlagsPageModel() throws Exception {
        Long id = 1L;
        FeatureFlag featureFlag = new FeatureFlag();
        featureFlag.setId(id);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<FeatureFlag> page = new PageImpl<>(List.of(featureFlag));
        when(adminFeatureFlagService.list(pageable)).thenReturn(page);

        mockMvc.perform(get("/api/v1/admin/feature-flags/")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id")
                .param("direction", "desc"))
                .andExpect(model().attribute("featureFlags", page))
                .andExpect(view().name("/admin/feature-flags"));
    }
}
