package com.featureflag.sdk.api;

import com.featureflag.shared.model.*;

import java.util.*;

public interface FeatureFlagClient {
    void initialize();
    boolean isEnabled(String featureFlagName, Map<String, String> criteria);
    Optional<List<FeatureFlag>> readAllFeatureFlags();
}
