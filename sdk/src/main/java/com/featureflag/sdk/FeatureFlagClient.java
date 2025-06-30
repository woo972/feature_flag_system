package com.featureflag.sdk;

import com.featureflag.shared.model.FeatureFlag;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
public class FeatureFlagClient {

    private FeatureFlagClientService featureFlagClientService = new FeatureFlagClientService();

    public List<FeatureFlag> getFeatureFlags() {
        return featureFlagClientService.getFeatureFlags();
    }
}
