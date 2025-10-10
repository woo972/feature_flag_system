package com.featureflag.sdk.cache;

import com.featureflag.shared.model.FeatureFlag;

import java.util.List;
import java.util.Optional;

public interface FeatureFlagCache {
    void load(Optional<List<FeatureFlag>> featureFlags);
    Optional<FeatureFlag> get(long featureFlagId);
    Optional<List<FeatureFlag>> readAll();
    void put(long featureFlagId, Optional<FeatureFlag> featureFlag);
}
