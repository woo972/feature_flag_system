package com.featureflag.core.controller;

import com.featureflag.core.service.FeatureFlagCommandService;
import com.featureflag.core.service.FeatureFlagQueryService;
import com.featureflag.core.service.FeatureFlagStreamProvider;
import com.featureflag.shared.api.RegisterFeatureFlagRequest;
import com.featureflag.shared.model.FeatureFlag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
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

    @GetMapping("/{flag-id}")
    public ResponseEntity<FeatureFlag> get(@PathVariable("flag-id") @Positive long flagId) {
        return ResponseEntity.ok(featureFlagQueryService.get(flagId));
    }

    @GetMapping("/page")
    public ResponseEntity<Page<FeatureFlag>> list(Pageable pageable) {
        return ResponseEntity.ok(featureFlagQueryService.list(pageable));
    }

    @PostMapping
    public ResponseEntity<FeatureFlag> register(@Valid @RequestBody RegisterFeatureFlagRequest request) {
        var featureFlag = featureFlagCommandService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(featureFlag);
    }

    @PostMapping("/{flag-id}/on")
    public ResponseEntity<FeatureFlag> on(@PathVariable("flag-id") @Positive long flagId) {
        return ResponseEntity.ok(featureFlagCommandService.on(flagId));
    }

    @PostMapping("/{flag-id}/off")
    public ResponseEntity<FeatureFlag> off(@PathVariable("flag-id") @Positive long flagId) {
        return ResponseEntity.ok(featureFlagCommandService.off(flagId));
    }

    @PostMapping("/{flag-id}/archive")
    public ResponseEntity<FeatureFlag> archive(@PathVariable("flag-id") @Positive long flagId) {
        return ResponseEntity.ok(featureFlagCommandService.archive(flagId));
    }

    @GetMapping("/evaluate/{flag-id}")
    public ResponseEntity<Boolean> evaluate(
            @PathVariable(value = "flag-id", required = true) @Positive long flagId,
            @RequestParam(value = "criteria", required = false) Map<@NotBlank String, @NotBlank String> criteria) {
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
