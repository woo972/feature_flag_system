package com.featureflag.sdk.api;

import com.featureflag.shared.model.FeatureFlag;
import org.jetbrains.annotations.*;

import java.util.List;
import java.util.Optional;

public interface FeatureFlagCache {
    void load(Optional<List<FeatureFlag>> featureFlags);
    Optional<FeatureFlag> get(String featureFlagName);
    Optional<List<FeatureFlag>> getFeatureFlags();
}
