package com.featureflag.sdk;

import lombok.extern.slf4j.Slf4j;
import java.util.Map;

@Slf4j
public class FeatureFlagClient {

    private final FeatureFlagClientService featureFlagClientService;

    public FeatureFlagClient(FeatureFlagClientService featureFlagClientService) {
        this.featureFlagClientService = featureFlagClientService;
    }


    public void initialize() {
        featureFlagClientService.initialize();
    }

    public boolean evaluate(String featureFlagName, Map<String, String> criteria) {
        return featureFlagClientService.evaluate(featureFlagName, criteria);
    }
}
