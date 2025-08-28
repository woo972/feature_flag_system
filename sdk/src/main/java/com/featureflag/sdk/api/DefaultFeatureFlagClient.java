package com.featureflag.sdk.api;


import lombok.*;

import java.util.*;

public class DefaultFeatureFlagClient implements FeatureFlagClient {
    private final FeatureFlagDataSource source;
    private final FeatureFlagCache cache;
    private final FeatureFlagChangeListener listener;

    @Builder
    public DefaultFeatureFlagClient(FeatureFlagDataSource source, FeatureFlagCache cache, FeatureFlagChangeListener listener) {
        this.source = source;
        this.cache = cache;
        this.listener = listener;
    }

    @Override
    public void initialize() {
        cache.initialize();
        listener.initialize();
        source.getAll().forEach(featureFlag -> {
            cache.put(featureFlag.getName(), featureFlag);
        });
    }

    @Override
    public boolean evaluate(String featureFlagName, Map<String, String> criteria) {
        return false;
    }
}
