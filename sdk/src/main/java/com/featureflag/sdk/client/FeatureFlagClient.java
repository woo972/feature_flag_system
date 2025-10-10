package com.featureflag.sdk.client;

import com.featureflag.shared.model.*;

import java.util.*;

public interface FeatureFlagClient {
    void initialize();
    boolean isEnabled(long featureFlagId, Map<String, String> criteria);
    Optional<List<FeatureFlag>> readAllFeatureFlags();
}
