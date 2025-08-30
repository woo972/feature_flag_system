package com.featureflag.sdk.api;

import java.util.Map;

public interface FeatureFlagClient {
    void initialize();
    boolean isEnabled(String featureFlagName, Map<String, String> criteria);
}
