package com.featureflag.sdk.api;

import com.featureflag.sdk.config.*;
import com.featureflag.shared.model.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;

import java.util.*;
import java.util.stream.*;

@Slf4j
public class DefaultFeatureFlagHttpDataSource implements FeatureFlagDataSource {
    private final FeatureFlagCoreHttpClient featureFlagCoreHttpClient;

    public DefaultFeatureFlagHttpDataSource(FeatureFlagCoreHttpClient featureFlagCoreHttpClient) {
        this.featureFlagCoreHttpClient = featureFlagCoreHttpClient;
    }

    @Override
    public FeatureFlag get(String featureFlagName) {
        return null;
    }

    @Override
    public Optional<List<FeatureFlag>> getFeatureFlags() {
        return getFeatureFlags(Optional.empty());
    }

    @Override
    public Optional<List<FeatureFlag>> getFeatureFlags(Optional<List<FeatureFlag>> featureFlags) {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", Collections.singletonList("application/json"));
        headers.put("ETag", Collections.singletonList(generateEtag(featureFlags)));

        return featureFlagCoreHttpClient.get(FeatureFlagProperty.GET_FEATURE_FLAGS_PATH, headers);
    }

    private String generateEtag(Optional<List<FeatureFlag>> featureFlags) {
        return featureFlags
                .map(flags -> flags.stream()
                        .sorted(Comparator.comparing(FeatureFlag::getName))
                        .map(FeatureFlag::getName)
                        .collect(Collectors.joining("|")))
                .map(content -> Integer.toHexString(content.hashCode()))
                .orElse(StringUtils.EMPTY);
    }
}
