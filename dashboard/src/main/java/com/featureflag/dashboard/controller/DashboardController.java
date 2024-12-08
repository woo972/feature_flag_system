package com.featureflag.dashboard.controller;

import com.featureflag.core.service.FeatureFlagService;
import com.featureflag.shared.model.FeatureFlag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    private final FeatureFlagService featureFlagService;

    public DashboardController(FeatureFlagService featureFlagService) {
        this.featureFlagService = featureFlagService;
    }

    @GetMapping
    public String dashboard(Model model) {
        // Add feature flags to the model
        return "dashboard";
    }

    @PostMapping("/flags")
    public String createFlag(@ModelAttribute FeatureFlag featureFlag) {
        // Create new feature flag
        return "redirect:/dashboard";
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
