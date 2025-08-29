package com.featureflag.sdk.api;

import com.fasterxml.jackson.core.*;
import com.featureflag.sdk.config.*;
import com.featureflag.shared.model.*;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class DefaultFeatureFlagListener implements FeatureFlagChangeListener {
    private static final long INITIAL_DELAY_SECONDS = 0;
    private static final long POLLING_INTERVAL_SECONDS = 10; // 10초마다 업데이트
    private static final Duration HTTP_TIMEOUT = Duration.ofSeconds(10);

    private final HttpClient client;
    private final ScheduledExecutorService scheduler;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    public DefaultFeatureFlagListener(String apiEndpoint) {
        this.client = FeatureFlagCoreHttpClient.get();

        this.scheduler = Executors.newSingleThreadScheduledExecutor(
                runnable -> new Thread(runnable, "feature-flag-listener")
        );
    }

    @Override
    public void initialize() {
        if (isRunning.compareAndSet(false, true)) {
            // 초기 지연 없이 바로 실행하고, 이후 주기적으로 실행
            scheduler.scheduleAtFixedRate(
                    this::fetchLatestFlags,
                    INITIAL_DELAY_SECONDS,
                    POLLING_INTERVAL_SECONDS,
                    TimeUnit.SECONDS
            );
            log.info("Feature flag polling started with interval: {} seconds", POLLING_INTERVAL_SECONDS);
        }
    }

    private void fetchLatestFlags() {
        try {
            var featureFlags = client.sendAsync(FeatureFlagCoreHttpClient.GET_FEATURE_FLAGS, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() == 200) {
                            String responseBody = response.body();
                            try {
                                log.debug("Successfully fetched latest feature flags");
                                return List.of(FeatureFlagCoreHttpClient.JSON_MAPPER.readValue(responseBody, FeatureFlag[].class));
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            log.warn("Failed to fetch feature flags. Status: {}", response.statusCode());
                        }
                        return null;
                    })
                    .exceptionally(ex -> {
                        log.error("Error while fetching feature flags: {}", ex.getMessage(), ex);
                        return null;
                    });

        } catch (Exception e) {
            log.error("Unexpected error in feature flag polling: {}", e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        if (isRunning.compareAndSet(true, false)) {
            try {
                log.info("Shutting down feature flag listener...");
                scheduler.shutdown();
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.warn("Forcing shutdown of feature flag listener...");
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Feature flag listener shutdown interrupted", e);
            }
        }
    }
}
