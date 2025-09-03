package com.featureflag.sdk.api;

import com.featureflag.shared.model.*;

import java.util.*;

public interface FeatureFlagDataSource {
    FeatureFlag get(String featureFlagName);
    Optional<List<FeatureFlag>> getFeatureFlags();
    Optional<List<FeatureFlag>> getFeatureFlags(Optional<List<FeatureFlag>> featureFlags);
}
