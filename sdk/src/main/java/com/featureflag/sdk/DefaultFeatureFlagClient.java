package com.featureflag.sdk;

import com.featureflag.sdk.api.FeatureFlagCache;
import com.featureflag.sdk.api.FeatureFlagClient;
import com.featureflag.sdk.api.FeatureFlagProvider;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;

@Slf4j
@Builder
public class DefaultFeatureFlagClient implements FeatureFlagClient {

    private final FeatureFlagCache cache;
    private final FeatureFlagProvider provider;

    @Override
    public void initialize() {
        cache.invalidate();
        var featureFlags = provider.fetchAll();
        cache.load(featureFlags);
    }

    @Override
    public boolean evaluate(String featureFlagName, Map<String, String> criteria) {
        return cache.get(featureFlagName)
                .map(featureFlag -> featureFlag.evaluate(criteria))
                .orElseGet(
                        () -> {
                            if (cache.isInitialized()) {
                                return false;
                            } else {
                                log.error("local cache for feature flag has not been initialized", featureFlagName);
                                return false;
                            }
                        }
                );
    }
}
