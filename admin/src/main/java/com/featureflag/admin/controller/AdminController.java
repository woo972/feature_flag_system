package com.featureflag.admin.controller;

import com.featureflag.core.service.FeatureFlagService;
import com.featureflag.core.service.RegisterFeatureFlagRequest;
import com.featureflag.shared.model.FeatureFlag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final FeatureFlagService featureFlagService;

    @GetMapping
    public String dashboard(Model model) {
        // Add feature flags to the model
        return "dashboard";
    }

    @PostMapping("/feature-flags")
    public ResponseEntity<Void> register(@RequestBody RegisterFeatureFlagRequest request) {
        featureFlagService.register(request);
        return ResponseEntity.created(null).build();
    }

    @PutMapping("/flags/{id}")
    public String updateFlag(@PathVariable String id, @ModelAttribute FeatureFlag featureFlag) {
        // Update existing feature flag
        return "redirect:/dashboard";
    }

    @DeleteMapping("/flags/{id}")
    public String deleteFlag(@PathVariable String id) {
        // Delete feature flag
        return "redirect:/dashboard";
    }
}
