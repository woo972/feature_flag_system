package com.featureflag.sdk.stream;

import com.featureflag.sdk.config.*;
import com.featureflag.shared.model.*;
import lombok.extern.slf4j.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.LongConsumer;

import static com.featureflag.sdk.config.FeatureFlagProperty.FEATURE_FLAG_STREAM_PATH;

@Slf4j
public class DefaultFeatureFlagStreamListener implements FeatureFlagStreamListener {

    private String clientId;
    private final ExecutorService executorService;
    private final FeatureFlagCoreHttpClient featureFlagCoreHttpClient;
    private volatile boolean isConnected = false;
    private volatile boolean isRunning = false;

    public DefaultFeatureFlagStreamListener() {
        this.executorService = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "feature-flag-stream-listener");
            thread.setDaemon(true);
            return thread;
        });
        this.featureFlagCoreHttpClient = new FeatureFlagCoreHttpClient();
    }

    @Override
    public void initialize(LongConsumer onFeatureFlagUpdated) {
        this.isRunning = true;
        CompletableFuture.runAsync(() -> {
            while (isRunning && !Thread.currentThread().isInterrupted()) {
                try {
                    var stream = connectStream();
                    listen(stream, onFeatureFlagUpdated);
                } catch (Exception e) {
                    log.error("Error in feature flag stream listener", e);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        log.error("Feature flag stream listener interrupted", ex);
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }, executorService);
    }

    private InputStream connectStream() {
        var response = featureFlagCoreHttpClient.connectStream(FEATURE_FLAG_STREAM_PATH, Map.of());

        if (response == null) {
            throw new RuntimeException("Failed to connect to feature flag stream");
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to connect to feature flag stream. Status code: " + response.statusCode());
        }

        this.isConnected = true;
        this.clientId = response.headers().firstValue("X-Client-Id").orElseThrow();

        return response.body();
    }

    private void listen(InputStream stream, LongConsumer onFeatureFlagUpdated) {
        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream))) {
            String eventType = null;
            StringBuilder dataBuilder = new StringBuilder();

            try {
                String line;
                while ((line = reader.readLine()) != null && isRunning) {
                    line = line.trim();

                    if (line.startsWith("event:")) {
                        eventType = line.substring(6).trim();
                        log.debug("Received SSE event type: {}", eventType);
                    } else if (line.startsWith("data:")) {
                        String data = line.substring(5).trim();
                        dataBuilder.append(data);
                    } else if (line.isEmpty()) {
                        // Empty line indicates end of event
                        if (eventType != null && dataBuilder.length() > 0) {
                            handleSseEvent(eventType, dataBuilder.toString(), onFeatureFlagUpdated);
                        }
                        // Reset for next event
                        eventType = null;
                        dataBuilder.setLength(0);
                    }
                }
            } catch (IOException e) {
                if (isRunning) {
                    log.error("Error reading SSE stream", e);
                    this.isConnected = false;
                }
            }
        }catch (Exception e) {
            log.error("Error in feature flag stream listener", e);
        }
    }

    private void handleSseEvent(String eventType, String data, LongConsumer onFeatureFlagUpdated) {
        log.debug("Handling SSE event: {} with data: {}", eventType, data);

        switch (eventType) {
            case "connected":
                log.info("SSE connection established: {}", data);
                break;
            case "feature-flag-updated":
                var updatedFeatureFlag = handleFeatureFlagUpdatedEvent(data);
                onFeatureFlagUpdated.accept(updatedFeatureFlag);
                break;
            default:
                log.debug("Unknown SSE event type: {}", eventType);
        }
    }

    private long handleFeatureFlagUpdatedEvent(String data) {
        FeatureFlag featureFlag = null;
        try {
            var objectMapper = JsonConfig.getObjectMapper();
            var eventNode = objectMapper.readTree(data);

            // Extract the FeatureFlag from the event source
            var sourceNode = eventNode.get("source");
            if (sourceNode != null) {
                featureFlag = objectMapper.treeToValue(sourceNode, FeatureFlag.class);
                log.info("Received feature flag update: {}", featureFlag);
            } else {
                log.warn("No source found in FeatureFlagUpdatedEvent data: {}", data);
            }
        } catch (Exception e) {
            log.error("Failed to parse FeatureFlagUpdatedEvent: {}", data, e);
        }

        if(featureFlag == null) {
            throw new RuntimeException("Failed to handle FeatureFlagUpdatedEvent: " + data);
        }

        return featureFlag.getId();
    }

    @Override
    public void close() {
        this.isRunning = false;
        executorService.shutdownNow();
    }
}
