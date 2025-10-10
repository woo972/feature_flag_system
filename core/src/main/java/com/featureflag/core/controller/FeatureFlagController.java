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
        if (request.checkNotModified(eTag)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(eTag).build();
        }
        return ResponseEntity.ok(featureFlagQueryService.findAll());
    }

    @GetMapping("/evaluate/{flag-id}")
    public ResponseEntity<Boolean> evaluate(
            @PathVariable(value = "flag-id", required = true) long flagId,
            @RequestParam(value = "criteria", required = false) Map<String, String> criteria) {
        return ResponseEntity.ok(featureFlagQueryService.evaluate(flagId, criteria));
    }

    @GetMapping("/cache/refresh")
    public ResponseEntity<Void> refreshCache() {
        featureFlagCommandService.refreshCache();
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/event-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> sse() {
        String clientId = UUID.randomUUID().toString();
        SseEmitter emitter = featureFlagStreamProvider.initiateConnection(clientId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Client-Id", clientId);
        return new ResponseEntity<>(emitter, headers, HttpStatus.OK);
    }
}
