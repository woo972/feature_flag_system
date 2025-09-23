package com.featureflag.core.service;

import com.featureflag.core.event.*;
import lombok.extern.slf4j.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.*;
import org.springframework.web.servlet.mvc.method.annotation.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Service
public class FeatureFlagStreamProvider {
    private static final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter initiateConnection(String clientId) {
        log.debug("streaming to client {}", clientId);
        SseEmitter sseEmitter = new SseEmitter(0L);

        emitters.put(clientId, sseEmitter);

        sseEmitter.onCompletion(() -> log.debug("emitter completed for client {}", clientId));
        sseEmitter.onTimeout(() -> {
            log.error("Client {} timed out from feature flag event stream", clientId);
            emitters.remove(clientId);
        });
        sseEmitter.onError((error) -> {
            log.error("Client {} encountered an error from feature flag event stream", clientId, error);
            emitters.remove(clientId);
        });

        try {
            sseEmitter.send(SseEmitter.event()
                    .name("connected")
                    .data("connection established for client: "+clientId)
                    .build());
        } catch (IOException e) {
            sseEmitter.completeWithError(e);
        }

        return sseEmitter;
    }

    @EventListener
    public void broadcastFeatureFlagUpdate(FeatureFlagUpdatedEvent event) {
        List<String> deadClients = new ArrayList<>();

        emitters.forEach((clientId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("feature-flag-updated")
                        .data(event)
                        .build());

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
