package com.featureflag.sdk.datasource;

import com.featureflag.sdk.config.FeatureFlagProperty;
import com.featureflag.shared.constants.HttpHeaderNames;
import com.featureflag.shared.constants.HttpHeaderValues;
import com.featureflag.shared.http.CoreApiException;
import com.featureflag.shared.http.CoreFeatureFlagClient;
import com.featureflag.shared.model.FeatureFlag;
import com.featureflag.shared.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class DefaultFeatureFlagHttpDataSource implements FeatureFlagDataSource {
    private final CoreFeatureFlagClient coreFeatureFlagClient;
    private String lastEtag;

    public DefaultFeatureFlagHttpDataSource(CoreFeatureFlagClient coreFeatureFlagClient) {
        this.coreFeatureFlagClient = coreFeatureFlagClient;
    }

    @Override
    public FeatureFlag get(long featureFlagId) {
        URI uri = URI.create(FeatureFlagProperty.FEATURE_FLAG_PATH + "/" + featureFlagId);
        try {
            return coreFeatureFlagClient.getJson(uri,
                    Map.of(HttpHeaderNames.ACCEPT, HttpHeaderValues.APPLICATION_JSON),
                    FeatureFlag.class);
        } catch (CoreApiException e) {
            log.error("Failed to fetch feature flag {} from core API", featureFlagId, e);
            return null;
        }
    }

    @Override
    public Optional<List<FeatureFlag>> getFeatureFlags() {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put(HttpHeaderNames.CONTENT_TYPE, List.of(HttpHeaderValues.APPLICATION_JSON));

        if (StringUtils.isNotEmpty(lastEtag)) {
            log.debug("send if-none-match header: {}", lastEtag);
            headers.put(HttpHeaderNames.IF_NONE_MATCH, List.of("\"" + lastEtag + "\""));
        }

        HttpResponse<String> response;
        try {
            response = coreFeatureFlagClient.getRaw(URI.create(FeatureFlagProperty.GET_FEATURE_FLAGS_PATH), headers);
        } catch (CoreApiException e) {
            log.error("Failed to call core API for feature flags", e);
            return Optional.empty();
        }

        if (response.statusCode() < 200 || response.statusCode() >= 400) {
            log.error("Failed to get feature flags. Status code: {}", response.statusCode());
            return Optional.empty();
        }

        if (response.statusCode() == 304) {
            log.debug("data not modified");
            return Optional.empty();
        }

        lastEtag = response.headers().firstValue(HttpHeaderNames.ETAG).orElse(null);

        return Optional.ofNullable(JsonUtils.readListValue(response.body(), FeatureFlag.class));
    }

}
