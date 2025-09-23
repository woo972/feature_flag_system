package com.featureflag.core.controller;

import com.featureflag.core.service.*;
import com.featureflag.shared.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/feature-flags")
public class FeatureFlagController {
    private final FeatureFlagQueryService featureFlagQueryService;
    private final FeatureFlagCommandService featureFlagCommandService;
    private final FeatureFlagStreamProvider featureFlagStreamProvider;

    @GetMapping
    public ResponseEntity<List<FeatureFlag>> list(WebRequest request) {
        var lastModifiedEpoch = featureFlagQueryService.getLastModifiedEpochTime();
        var eTag = "\"" + lastModifiedEpoch + "\"";
        if(request.checkNotModified(eTag)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(eTag).build();
        }
        return ResponseEntity.ok(featureFlagQueryService.findAll());
    }

    @GetMapping("/evaluate/{flag-id}")
    public ResponseEntity<Boolean> evaluate(
            @PathVariable(value = "flag-id", required = true) Long flagId,
            @RequestParam(value = "criteria", required = false) Map<String, String> criteria) {
        return ResponseEntity.ok(featureFlagQueryService.evaluate(flagId, criteria));
    }

    @GetMapping("/cache/refresh")
    public ResponseEntity<Void> refreshCache() {
        featureFlagCommandService.refreshCache();
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/connect-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sse(@PathVariable(value = "client-id", required = true) String clientId) {
        return featureFlagStreamProvider.initiateConnection(clientId);
    }
}
