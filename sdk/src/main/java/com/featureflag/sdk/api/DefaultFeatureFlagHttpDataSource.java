package com.featureflag.sdk.api;

import com.featureflag.sdk.config.*;
import com.featureflag.shared.model.*;
import lombok.extern.slf4j.*;
import java.util.*;

@Slf4j
public class DefaultFeatureFlagHttpDataSource implements FeatureFlagDataSource {
    private final FeatureFlagCoreHttpClient featureFlagCoreHttpClient;

    public DefaultFeatureFlagHttpDataSource(FeatureFlagCoreHttpClient featureFlagCoreHttpClient){
        this.featureFlagCoreHttpClient = featureFlagCoreHttpClient;
    }

    @Override
    public FeatureFlag get(String featureFlagName) {
        return null;
    }

    @Override
    public List<FeatureFlag> getFeatureFlags() {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", Collections.singletonList("application/json"));
        headers.put("ETag", Collections.singletonList(""));

        return featureFlagCoreHttpClient.get(FeatureFlagProperty.GET_FEATURE_FLAGS_PATH, headers);

    }
}
