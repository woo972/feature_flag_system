package com.featureflag.admin.controller;

import com.featureflag.core.service.FeatureFlagService;
import com.featureflag.core.service.RegisterFeatureFlagRequest;
import com.featureflag.shared.model.FeatureFlag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {
    private final FeatureFlagService featureFlagService;

    @GetMapping("/feature-flags")
    public String list(Model model) {
        model.addAttribute("featureFlags", featureFlagService.list());
        return "/admin/feature-flags";
    }

    @GetMapping("/feature-flags/{id}")
    public String get(@PathVariable(value = "id", required = true) Long id,
                                    Model model) {
        model.addAttribute("featureFlag", featureFlagService.get(id));
        return "/admin/feature-flag-detail";
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
