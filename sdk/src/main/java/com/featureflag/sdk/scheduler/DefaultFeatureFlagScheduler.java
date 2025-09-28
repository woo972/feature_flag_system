package com.featureflag.sdk.scheduler;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class DefaultFeatureFlagScheduler implements FeatureFlagChangeScheduler {
    private static final long INITIAL_DELAY_SECONDS = 0;
    private static final long POLLING_INTERVAL_SECONDS = 10;

    private final ScheduledExecutorService scheduler;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    public DefaultFeatureFlagScheduler() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor(
                runnable -> new Thread(runnable, "feature-flag-scheduler")
        );
    }

    @Override
    public void initialize(Runnable runnable) {
        if (isRunning.compareAndSet(false, true)) {
            scheduler.scheduleAtFixedRate(
                    runnable,
                    INITIAL_DELAY_SECONDS,
                    POLLING_INTERVAL_SECONDS,
                    TimeUnit.SECONDS
            );
            log.info("Feature flag polling started with interval: {} seconds", POLLING_INTERVAL_SECONDS);
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
