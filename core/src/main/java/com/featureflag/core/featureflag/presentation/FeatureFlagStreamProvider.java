package com.featureflag.core.featureflag.presentation;

import com.featureflag.core.featureflag.domain.event.FeatureFlagUpdatedEvent;
import com.featureflag.core.featureflag.presentation.mapper.FeatureFlagResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class FeatureFlagStreamProvider {
    private final FeatureFlagResponseMapper featureFlagResponseMapper;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter initiateConnection(String clientId) {
        log.debug("streaming to client {}", clientId);
        var sseEmitter = new SseEmitter(0L);
        emitters.put(clientId, sseEmitter);

        sseEmitter.onCompletion(() -> emitters.remove(clientId));
        sseEmitter.onTimeout(() -> {
            log.error("Client {} timed out from feature flag event stream", clientId);
            emitters.remove(clientId);
        });
        sseEmitter.onError(error -> {
            log.error("Client {} encountered an error from feature flag event stream", clientId, error);
            emitters.remove(clientId);
        });

        try {
            sseEmitter.send(SseEmitter.event()
                    .name("connected")
                    .data("connection established for client: " + clientId));
        } catch (IOException e) {
            sseEmitter.completeWithError(e);
        }

        return sseEmitter;
    }

    @EventListener
    public void broadcastFeatureFlagUpdate(FeatureFlagUpdatedEvent event) {
        List<String> deadClients = new ArrayList<>();
        var payload = featureFlagResponseMapper.toResponse(event.featureFlag());

        emitters.forEach((clientId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("feature-flag-updated")
                        .data(payload));
                log.debug("emit feature flag update to client: {}", clientId);
            } catch (IOException e) {
                emitter.completeWithError(e);
                deadClients.add(clientId);
                log.error("Client {} encountered an error from feature flag event stream", clientId, e);
            }
        });

        deadClients.forEach(emitters::remove);
    }
}
