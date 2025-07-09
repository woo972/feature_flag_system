package com.featureflag.sdk;

import com.featureflag.sdk.api.FeatureFlagCache;
import com.featureflag.sdk.api.FeatureFlagClient;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

@Slf4j
public class SimpleFeatureFlagClient implements FeatureFlagClient, AutoCloseable {

    private FeatureFlagCache cache;
    private FeatureFlagProvider provider;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> future;

    @Builder
    public SimpleFeatureFlagClient(FeatureFlagCache cache, FeatureFlagProvider provider) {
        this.cache = cache;
        this.provider = provider;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(
                runnable -> {
                    Thread t = new Thread(runnable, "feature-flag-updater");
                    t.setDaemon(true);
                    return t;
                }
        );
        this.future = scheduler
                .scheduleAtFixedRate(this::update, 60, 60, java.util.concurrent.TimeUnit.SECONDS);
    }

    @Override
    public void initialize() {
        cache.invalidate();
        var featureFlags = provider.fetchAll();
        cache.load(featureFlags);
        log.info("initialized feature flag list. Size: {}", featureFlags.size());
    }

    @Override
    public boolean evaluate(String featureFlagName, Map<String, String> criteria) {
        var result = cache.get(featureFlagName)
                .map(featureFlag -> featureFlag.evaluate(criteria))
                .orElseGet(
                        () -> {
                            if (cache.isInitialized()) {
                                return false;
                            } else {
                                log.error("local cache for feature flag: {} has not been initialized", featureFlagName);
                                return false;
                            }
                        }
                );
        log.debug("evaluated feature flag: {}, result: {}", featureFlagName, result);
        return result;
    }

    public void update() {
        cache.load(provider.fetchAll());
        log.debug("updating feature flag cache");
    }

    @Override
    public void close() {
        if (future != null && !future.isDone()) {
            future.cancel(true);
        }

        if(scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if(!scheduler.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)){
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                scheduler.shutdownNow();
            }

        }
    }
}
