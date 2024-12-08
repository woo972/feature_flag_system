package com.featureflag.core.controller;

import com.featureflag.core.service.FeatureFlagService;
import com.featureflag.shared.model.FeatureFlag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class FeatureFlagController {
    private final FeatureFlagService featureFlagService;

    public FeatureFlagController(FeatureFlagService featureFlagService) {
        this.featureFlagService = featureFlagService;
    }

    @GetMapping("/evaluate/{flagId}")
    public ResponseEntity<Boolean> evaluateFlag(
            @PathVariable Long flagId,
            @RequestParam Map<String, String> criteria) {
        return ResponseEntity.ok(featureFlagService.evaluateFlag(flagId, criteria));
    }

    @GetMapping("/cache/refresh")
    public ResponseEntity<Void> refreshCache() {
        featureFlagService.refreshCache();
        return ResponseEntity.ok().build();
    }
}
