package com.featureflag.sdk.api;

import com.featureflag.sdk.config.*;
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
        if (source == null) {
            source = new DefaultFeatureFlagHttpDataSource(new FeatureFlagCoreHttpClient());
        }
        if (cache == null) {
            cache = new DefaultFeatureFlagLocalCache();
        }
        if (scheduler == null) {
            scheduler = new DefaultFeatureFlagScheduler();
        }
        this.source = source;
        this.cache = cache;
        this.scheduler = scheduler;
        log.info("Feature flag client initialized. data source: {}, cache: {}, scheduler: {}",
                source.getClass().getSimpleName(),
                cache.getClass().getSimpleName(),
                scheduler.getClass().getSimpleName());
    }

    @Override
    public void initialize() {
        var featureFlags = source.getFeatureFlags();
        cache.initialize(featureFlags);
        scheduler.initialize(() -> {
            var cachedFeatureFlags = cache.getFeatureFlags();
            cachedFeatureFlags = source.getFeatureFlags(cachedFeatureFlags);
            cache.load(cachedFeatureFlags);
        });
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
