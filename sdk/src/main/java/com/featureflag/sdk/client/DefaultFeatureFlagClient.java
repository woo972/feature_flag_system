package com.featureflag.sdk.client;

import com.featureflag.sdk.cache.*;
import com.featureflag.sdk.config.*;
import com.featureflag.sdk.datasource.*;
import com.featureflag.sdk.scheduler.*;
import com.featureflag.sdk.stream.*;
import com.featureflag.shared.model.*;
import lombok.*;
import lombok.extern.slf4j.*;
import java.util.*;

@Slf4j
public class DefaultFeatureFlagClient implements FeatureFlagClient {
    private final FeatureFlagDataSource source;
    private final FeatureFlagCache cache;
    private final UpdateMode updateMode;
    private FeatureFlagScheduler scheduler;
    private FeatureFlagStreamListener listener;

    @Builder
    public DefaultFeatureFlagClient(FeatureFlagDataSource source, FeatureFlagCache cache, UpdateMode updateMode) {
        if (source == null) {
            source = new DefaultFeatureFlagHttpDataSource(new FeatureFlagCoreHttpClient());
        }
        if (cache == null) {
            cache = new DefaultFeatureFlagLocalCache();
        }
        if(updateMode == null) {
            updateMode = UpdateMode.STREAM;
        }

        this.source = source;
        this.cache = cache;
        this.updateMode = updateMode;

        if(UpdateMode.POLLING.equals(updateMode)) {
            this.scheduler = new DefaultFeatureFlagScheduler();
        }else if (UpdateMode.STREAM.equals(updateMode)) {
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
        } else if (UpdateMode.STREAM.equals(updateMode)) {
            var updatedFeatureFlags = source.getFeatureFlags();
            cache.load(updatedFeatureFlags);
            listener.initialize(changedFeatureFlagId ->
                    cache.put(changedFeatureFlagId, Optional.ofNullable(source.get(changedFeatureFlagId)))
            );
        }
    }

    @Override
    public boolean isEnabled(long featureFlagId, Map<String, String> criteria) {
        var result = cache.get(featureFlagId)
                .map(featureFlag -> featureFlag.evaluate(criteria))
                .orElseGet(
                        () -> {
                            log.error("Feature flag id {} is not found in feature flag cache", featureFlagId);
                            return false;
                        }
                );
        log.debug("evaluated feature flag id: {}, result: {}", featureFlagId, result);
        return result;
    }

    @Override
    public Optional<List<FeatureFlag>> readAllFeatureFlags() {
        return cache.readAll();
    }

    public Map<String, String> activatedParameters(){
        String schedulerClassName = scheduler == null ? "null" : scheduler.getClass().getSimpleName();
        String listenerClassName = listener == null ? "null" : listener.getClass().getSimpleName();
        return new HashMap<String, String>() {{
           put("source", source.getClass().getSimpleName());
           put("cache", cache.getClass().getSimpleName());
           put("updateMode", updateMode.name());
           put("scheduler", schedulerClassName);
           put("listener", listenerClassName);
        }};
    }
}
