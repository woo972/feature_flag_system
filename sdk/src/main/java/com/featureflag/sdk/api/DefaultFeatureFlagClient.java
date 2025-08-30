package com.featureflag.sdk.api;

import lombok.*;
import lombok.extern.slf4j.*;
import java.util.*;

@Slf4j
public class DefaultFeatureFlagClient implements FeatureFlagClient {
    private final FeatureFlagDataSource source;
    private final FeatureFlagCache cache;
    private final FeatureFlagChangeScheduler scheduler;

    @Builder
    public DefaultFeatureFlagClient(FeatureFlagDataSource source, FeatureFlagCache cache, FeatureFlagChangeScheduler scheduler) {
        this.source = source;
        this.cache = cache;
        this.scheduler = scheduler;
    }

    @Override
    public void initialize() {
        var featureFlags = source.getAll();
        cache.initialize(featureFlags);
        scheduler.initialize(() -> cache.load(source.getAll()));
    }

    @Override
    public boolean isEnabled(String featureFlagName, Map<String, String> criteria) {
        var result = cache.get(featureFlagName)
                .map(featureFlag -> featureFlag.evaluate(criteria))
                .orElseGet(
                        () -> {
                            log.error("{} is not found in feature flag cache", featureFlagName);
                            return false;
                        }
                );
        log.debug("evaluated feature flag: {}, result: {}", featureFlagName, result);
        return result;
    }
}
