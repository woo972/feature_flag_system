package com.featureflag.sdk;

import com.featureflag.sdk.api.FeatureFlagCache;
import com.featureflag.sdk.api.FeatureFlagClient;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;

@Slf4j
public class SimpleFeatureFlagClient implements FeatureFlagClient {

    private FeatureFlagCache cache;
    private FeatureFlagProvider provider;
    private ScheduledFuture<?> future;

    @Builder
    public SimpleFeatureFlagClient(FeatureFlagCache cache, FeatureFlagProvider provider) {
        this.cache = cache;
        this.provider = provider;
        this.future = Executors
                .newSingleThreadScheduledExecutor()
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
}
