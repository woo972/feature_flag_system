package com.featureflag.core.controller;

import com.featureflag.core.event.FeatureFlagUpdatedEvent;
import com.featureflag.core.service.FeatureFlagService;
import com.featureflag.shared.config.JacksonConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/feature-flags")
public class FeatureFlagController {
    // TODO: Replace with Redis
    private static final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

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

    @GetMapping(path = "/event-stream/{client-id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sse(@PathVariable(value = "client-id", required = true) String clientId) {
        log.debug("streaming to client {}", clientId);
        SseEmitter sseEmitter = new SseEmitter();

        emitters.put(clientId, sseEmitter);

        sseEmitter.onCompletion(() -> log.debug("emitter completed for client {}", clientId));
        sseEmitter.onTimeout(() -> log.error("Client {} timed out from feature flag event stream", clientId));
        sseEmitter.onError((error) -> log.error("Client {} encountered an error from feature flag event stream", clientId, error));

        try {
            sseEmitter.send(SseEmitter.event()
                    .name("ping")
                    .data("connection established")
                    .build());
        } catch (IOException e) {
            sseEmitter.completeWithError(e);
        }

        return sseEmitter;
    }

    @EventListener
    public void handleFeatureFlagUpdated(FeatureFlagUpdatedEvent event) {
        log.debug("feature flag updated: {}", event);
        emitters.forEach((clientId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("feature-flag-updated")
                        .data(JacksonConfig.getObjectMapper().writeValueAsString(event))
                        .build());
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        });
    }
}
