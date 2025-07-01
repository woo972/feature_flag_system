package com.featureflag.sdk;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class FeatureFlagClient {

    private final FeatureFlagClientService featureFlagClientService = new FeatureFlagClientService();

    public void initialize() {
        featureFlagClientService.initialize();
    }

    public boolean evaluate(String featureFlagName, Map<String, String> criteria) {
        return featureFlagClientService.evaluate(featureFlagName, criteria);
    }
}
