package com.featureflag.sdk.api;

import com.featureflag.shared.model.FeatureFlag;

import java.util.List;
import java.util.Optional;

public interface FeatureFlagCache {
    void load(List<FeatureFlag> featureFlags);
    Optional<FeatureFlag> get(String featureFlagName);
    void invalidate();
    boolean isInitialized();
    void update(List<FeatureFlag> featureFlags);
    void put(String featureFlagName, FeatureFlag featureFlag);
    void initialize();
}
