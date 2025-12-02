package com.featureflag.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.featureflag.admin.service.AdminFeatureFlagService;
import com.featureflag.shared.api.RegisterFeatureFlagRequest;
import com.featureflag.shared.model.FeatureFlag;
import com.featureflag.shared.model.FeatureFlagStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = AdminController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {
    @MockBean
    private AdminFeatureFlagService adminFeatureFlagService;

    @MockBean
    private com.featureflag.admin.service.AdminPreDefinedTargetingRuleService preDefinedRuleService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("returns home page")
    @Test
    public void returnsHomeView() throws Exception {
        var featureFlag = createFeatureFlag();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<FeatureFlag> page = new PageImpl<>(List.of(featureFlag));
        when(adminFeatureFlagService.list(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/admin"))
                .andExpect(model().attribute("featureFlagPage", page))
                .andExpect(view().name("featureflags/dashboard"));
    }

    @DisplayName("returns register page")
    @Test
    public void returnsRegisterView() throws Exception {
        when(preDefinedRuleService.list()).thenReturn(List.of());
        mockMvc.perform(get("/admin/feature-flags/new"))
                .andExpect(view().name("featureflags/form"));
    }

    @DisplayName("create feature flags")
    @Test
    public void createFlag() throws Exception {
        var request = RegisterFeatureFlagRequest.builder()
                .name("feature-1")
                .description("desc")
                .targetingRules(List.of())
                .build();

        doNothing().when(adminFeatureFlagService).register(any(RegisterFeatureFlagRequest.class));

        mockMvc.perform(post("/admin/feature-flags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(flash().attribute("Success", "Feature flag registered."))
                .andExpect(redirectedUrl("/admin"));
    }

    @DisplayName("returns feature-flag-detail page & model")
    @Test
    public void returnsFeatureFlagModel() throws Exception {
        long id = 1L;
        var featureFlag = createFeatureFlag();
        when(adminFeatureFlagService.get(id)).thenReturn(featureFlag);

        mockMvc.perform(get("/admin/feature-flags/" + id))
                .andExpect(model().attribute("featureFlag", featureFlag))
                .andExpect(view().name("featureflags/detail"));
    }

    @DisplayName("returns feature-flag-detail page & model with on")
    @Test
    public void returnsFeatureFlagModelWithOn() throws Exception {
        long id = 1L;
        var featureFlag = createFeatureFlag();
        when(adminFeatureFlagService.on(id)).thenReturn(featureFlag);

        mockMvc.perform(post("/admin/feature-flags/" + id + "/on"))
                .andExpect(redirectedUrl("/admin/feature-flags/" + id));
    }

    @DisplayName("returns feature-flag-detail page & model with off")
    @Test
    public void returnsFeatureFlagModelWithOff() throws Exception {
        long id = 1L;
        var featureFlag = createOffFeatureFlag();
        when(adminFeatureFlagService.off(id)).thenReturn(featureFlag);

        mockMvc.perform(post("/admin/feature-flags/" + id + "/off"))
                .andExpect(redirectedUrl("/admin/feature-flags/" + id));
    }

    @DisplayName("returns feature-flag-detail page & model with archived")
    @Test
    public void returnsFeatureFlagModelWithArchived() throws Exception {
        long id = 1L;
        var featureFlag = createFeatureFlag();
        when(adminFeatureFlagService.archive(id)).thenReturn(featureFlag);

        mockMvc.perform(post("/admin/feature-flags/" + id + "/archive"))
                .andExpect(redirectedUrl("/admin/feature-flags/" + id));
    }

    private FeatureFlag createFeatureFlag() {
        return FeatureFlag
                .builder()
                .id(1L)
                .name("feature-1")
                .description("desc")
                .status(FeatureFlagStatus.ON)
                .createdAt(LocalDateTime.MAX)
                .build();
    }

    private FeatureFlag createOffFeatureFlag() {
        return FeatureFlag
                .builder()
                .id(1L)
                .name("feature-1")
                .description("desc")
                .status(FeatureFlagStatus.OFF)
                .createdAt(LocalDateTime.MAX)
                .build();
    }

    private FeatureFlag createArchivedFeatureFlag() {
        return FeatureFlag
                .builder()
                .id(1L)
                .name("feature-1")
                .description("desc")
                .status(FeatureFlagStatus.OFF)
                .createdAt(LocalDateTime.MAX)
                .archivedAt(LocalDateTime.MAX)
                .build();
    }
}
