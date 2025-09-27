package com.featureflag.sdk.api;

import com.featureflag.sdk.config.*;
import com.featureflag.shared.model.*;
import lombok.*;
import lombok.extern.slf4j.*;
import java.util.*;

@Slf4j
public class DefaultFeatureFlagClient implements FeatureFlagClient {
    private final FeatureFlagDataSource source;
    private final FeatureFlagCache cache;
    private final UpdateMode updateMode;
    private FeatureFlagChangeScheduler scheduler;
    private FeatureFlagChangeStreamListener listener;

    @Builder
    public DefaultFeatureFlagClient(FeatureFlagDataSource source, FeatureFlagCache cache, UpdateMode updateMode) {
        if (source == null) {
            source = new DefaultFeatureFlagHttpDataSource(new FeatureFlagCoreHttpClient());
        }
        if (cache == null) {
            cache = new DefaultFeatureFlagLocalCache();
        }

        this.source = source;
        this.cache = cache;
        this.updateMode = updateMode;

        if(updateMode == null || UpdateMode.POLLING.equals(updateMode)) {
            this.scheduler = new DefaultFeatureFlagScheduler();
        }else if (UpdateMode.POLLING.equals(updateMode)) {
            this.listener = new DefaultFeatureFlagStreamListener();
        }

        log.info("Feature flag client initialized. data source: {}, cache: {}, update mode: {}",
                source.getClass().getSimpleName(),
                cache.getClass().getSimpleName(),
                updateMode);
    }

    @Override
    public void initialize() {
        if (UpdateMode.POLLING.equals(updateMode)) {
            scheduler.initialize(() -> {
                var updatedFeatureFlags = source.getFeatureFlags();
                cache.load(updatedFeatureFlags);
            });
        }else if (UpdateMode.STREAM.equals(updateMode)) {
            var updatedFeatureFlags = source.getFeatureFlags();
            cache.load(updatedFeatureFlags);
            listener.initialize((changedFeatureFlagName) -> {
                var featureFlag = cache.get(changedFeatureFlagName);
                cache.put(changedFeatureFlagName, featureFlag);
            });
        }
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

    @Override
    public Optional<List<FeatureFlag>> readAllFeatureFlags() {
        return cache.readAll();
    }
}
