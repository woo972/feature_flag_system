package com.featureflag.core.controller;

import com.featureflag.core.service.FeatureFlagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/feature-flags")
public class FeatureFlagController {
    private final FeatureFlagService featureFlagService;

    @GetMapping("/evaluate/{flag-id}")
    public ResponseEntity<Boolean> evaluate(
            @PathVariable(value = "flag-id", required = true) Long flagId,
            @RequestParam(value = "criteria", required = false) Map<String, String> criteria) {
        return ResponseEntity.ok(featureFlagService.evaluate(flagId, criteria));
    }

    @GetMapping("/cache/refresh")
    public ResponseEntity<Void> refreshCache() {
        featureFlagService.refreshCache();
        return ResponseEntity.ok().build();
    }
}
