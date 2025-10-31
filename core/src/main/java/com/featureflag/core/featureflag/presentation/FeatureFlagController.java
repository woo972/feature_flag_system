package com.featureflag.core.featureflag.presentation;

import com.featureflag.core.featureflag.application.command.FeatureFlagCommandService;
import com.featureflag.core.featureflag.application.query.FeatureFlagQueryService;
import com.featureflag.core.featureflag.presentation.dto.FeatureFlagResponse;
import com.featureflag.core.featureflag.presentation.mapper.FeatureFlagResponseMapper;
import com.featureflag.shared.api.RegisterFeatureFlagRequest;
import com.featureflag.shared.constants.HttpHeaderNames;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/v1/feature-flags")
public class FeatureFlagController {
    private final FeatureFlagQueryService featureFlagQueryService;
    private final FeatureFlagCommandService featureFlagCommandService;
    private final FeatureFlagStreamProvider featureFlagStreamProvider;
    private final FeatureFlagResponseMapper featureFlagResponseMapper;

    @GetMapping
    public ResponseEntity<List<FeatureFlagResponse>> list(WebRequest request) {
        var lastModifiedEpoch = featureFlagQueryService.getLastModifiedEpochTime();
        var eTag = "\"" + lastModifiedEpoch + "\"";
        if (request.checkNotModified(eTag)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(eTag).build();
        }
        var featureFlags = featureFlagQueryService.findAll();
        return ResponseEntity.ok()
                .eTag(eTag)
                .body(featureFlagResponseMapper.toResponse(featureFlags));
    }

    @GetMapping("/{flag-id}")
    public ResponseEntity<FeatureFlagResponse> get(@PathVariable("flag-id") @Positive long flagId) {
        var featureFlag = featureFlagQueryService.get(flagId);
        return ResponseEntity.ok(featureFlagResponseMapper.toResponse(featureFlag));
    }

    @GetMapping("/page")
    public ResponseEntity<Page<FeatureFlagResponse>> list(Pageable pageable) {
        var page = featureFlagQueryService.list(pageable);
        return ResponseEntity.ok(featureFlagResponseMapper.toResponse(page));
    }

    @PostMapping
    public ResponseEntity<FeatureFlagResponse> register(@Valid @RequestBody RegisterFeatureFlagRequest request) {
        var featureFlag = featureFlagCommandService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(featureFlagResponseMapper.toResponse(featureFlag));
    }

    @PostMapping("/{flag-id}/on")
    public ResponseEntity<FeatureFlagResponse> on(@PathVariable("flag-id") @Positive long flagId) {
        var featureFlag = featureFlagCommandService.on(flagId);
        return ResponseEntity.ok(featureFlagResponseMapper.toResponse(featureFlag));
    }

    @PostMapping("/{flag-id}/off")
    public ResponseEntity<FeatureFlagResponse> off(@PathVariable("flag-id") @Positive long flagId) {
        var featureFlag = featureFlagCommandService.off(flagId);
        return ResponseEntity.ok(featureFlagResponseMapper.toResponse(featureFlag));
    }

    @PostMapping("/{flag-id}/archive")
    public ResponseEntity<FeatureFlagResponse> archive(@PathVariable("flag-id") @Positive long flagId) {
        var featureFlag = featureFlagCommandService.archive(flagId);
        return ResponseEntity.ok(featureFlagResponseMapper.toResponse(featureFlag));
    }

    @GetMapping("/evaluate/{flag-id}")
    public ResponseEntity<Boolean> evaluate(
            @PathVariable("flag-id") @Positive long flagId,
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
        var clientId = UUID.randomUUID().toString();
        var emitter = featureFlagStreamProvider.initiateConnection(clientId);
        var headers = new HttpHeaders();
        headers.add(HttpHeaderNames.X_CLIENT_ID, clientId);
        return new ResponseEntity<>(emitter, headers, HttpStatus.OK);
    }
}
