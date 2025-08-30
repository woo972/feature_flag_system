package com.featureflag.sdk.api;

import com.featureflag.shared.model.FeatureFlag;

import java.util.List;
import java.util.Optional;

public interface FeatureFlagCache {
    void load(List<FeatureFlag> featureFlags);
    Optional<FeatureFlag> get(String featureFlagName);
    void invalidate();
    void initialize(List<FeatureFlag> featureFlags);
}
