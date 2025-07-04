package com.featureflag.sdk.api;

import java.util.Map;

public interface FeatureFlagClient {
    void initialize();
    boolean evaluate(String featureFlagName, Map<String, String> criteria);
}
