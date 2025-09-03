package com.featureflag.sdk.api;

import com.featureflag.shared.model.FeatureFlag;

import java.util.List;
import java.util.Optional;

public interface FeatureFlagCache {
    void load(Optional<List<FeatureFlag>> featureFlags);
    Optional<FeatureFlag> get(String featureFlagName);
    void invalidate();
    void initialize(Optional<List<FeatureFlag>> featureFlags);
    Optional<List<FeatureFlag>> getFeatureFlags();
}
