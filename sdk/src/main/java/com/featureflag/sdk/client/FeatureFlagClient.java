package com.featureflag.sdk.client;

import com.featureflag.shared.model.FeatureFlag;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FeatureFlagClient {
    void initialize();
    boolean isEnabled(long featureFlagId, Map<String, String> criteria);
    Optional<List<FeatureFlag>> readAllFeatureFlags();
}
