package com.featureflag.sdk.api;

import com.featureflag.sdk.config.*;
import com.featureflag.shared.model.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import java.util.*;

@Slf4j
public class DefaultFeatureFlagHttpDataSource implements FeatureFlagDataSource {
    private final FeatureFlagCoreHttpClient featureFlagCoreHttpClient;
    private String lastEtag;

    public DefaultFeatureFlagHttpDataSource(FeatureFlagCoreHttpClient featureFlagCoreHttpClient) {
        this.featureFlagCoreHttpClient = featureFlagCoreHttpClient;
    }

    @Override
    public FeatureFlag get(String featureFlagName) {
        return null;
    }

    @Override
    public Optional<List<FeatureFlag>> getFeatureFlags() {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", Collections.singletonList("application/json"));

        if (StringUtils.isNotEmpty(lastEtag)) {
            log.debug("send if-none-match header: {}", lastEtag);
            headers.put("If-None-Match", Collections.singletonList("\"" + lastEtag + "\""));
        }

        var response = featureFlagCoreHttpClient.get(FeatureFlagProperty.GET_FEATURE_FLAGS_PATH, headers);
        if (response.statusCode() < 200 && response.statusCode() >= 400) {
            log.error("Failed to get feature flags. Status code: {}", response.statusCode());
            return Optional.empty();
        }

        if (response.statusCode() == 304) {
            log.debug("data not modified");
            return Optional.empty();
        }

        lastEtag = response.headers().firstValue("ETag").orElse(null);

        return JsonConfig
                .readValue(Optional.ofNullable(response.body()).orElse(StringUtils.EMPTY));
    }

}
