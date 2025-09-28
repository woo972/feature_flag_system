package com.featureflag.sdk.cache;

import com.featureflag.shared.model.FeatureFlag;

import java.util.List;
import java.util.Optional;

public interface FeatureFlagCache {
    void load(Optional<List<FeatureFlag>> featureFlags);
    Optional<FeatureFlag> get(String featureFlagName);
    Optional<List<FeatureFlag>> readAll();
    void put(String featureFlagName, Optional<FeatureFlag> featureFlag);
}
