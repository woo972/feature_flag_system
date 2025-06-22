package com.featureflag.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.featureflag.core.service.FeatureFlagService;
import com.featureflag.core.service.RegisterFeatureFlagRequest;
import com.featureflag.shared.model.FeatureFlag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    @DisplayName("returns feature-flag model ")
    @Test
    public void returnsFeatureFlagModel() throws Exception {
        Long id = 1L;
        FeatureFlag featureFlag = new FeatureFlag();
        featureFlag.setId(id);
        when(featureFlagService.get(id)).thenReturn(featureFlag);

        mockMvc.perform(get("/api/v1/admin/feature-flags/"+id))
                .andExpect(model().attribute("featureFlag", featureFlag))
                .andExpect(view().name("/admin/feature-flag-detail"));
    }

//    @DisplayName("returns feature-flag models")
//    @Test
//    public void returnsFeatureFlagModels() throws Exception {
//        Long id = 1L;
//        FeatureFlag featureFlag = new FeatureFlag();
//        featureFlag.setId(id);
//        List<FeatureFlag> list = List.of(featureFlag);
//        when(featureFlagService.list()).thenReturn(list);
//
//        mockMvc.perform(get("/api/v1/admin/feature-flags"))
//                .andExpect(model().attribute("featureFlags", list))
//                .andExpect(view().name("/admin/feature-flags"));
//    }
}
