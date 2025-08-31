package com.featureflag.sdk.api;

import com.featureflag.sdk.config.*;
import com.featureflag.shared.model.*;
import lombok.extern.slf4j.*;
import java.io.*;
import java.net.http.*;
import java.util.*;

@Slf4j
public class DefaultFeatureFlagHttpDataSource implements FeatureFlagDataSource {
    @Override
    public FeatureFlag get(String featureFlagName) {
        return null;
    }

    @Override
    public List<FeatureFlag> getAll() {
        // TODO: etag header 추가하여 비교
        try {
            var retryCount = 0;
            HttpResponse<String> response = null;
            while (retryCount < FeatureFlagCoreHttpClient.MAX_RETRIES) {
                response = FeatureFlagCoreHttpClient.get().send(
                        FeatureFlagCoreHttpClient.GET_FEATURE_FLAGS,
                        HttpResponse.BodyHandlers.ofString()
                );

                log.debug("Feature flags fetched. status code: {}", response.statusCode());
                log.debug("Feature flags fetched. response body: {}", response.body());

                if (response.statusCode() >= 200 && response.statusCode() < 400) {
                    if (response.statusCode() == 304) {
                        log.debug("Feature flags not modified");
                        return List.of();
                    }
                    return List.of(JsonConfig.getObjectMapper().readValue(response.body(), FeatureFlag[].class));
                }
                if (response.statusCode() < 200 && response.statusCode() >= 400) {
                    retryCount++;
                }
            }
            return List.of();
        } catch (IOException | InterruptedException e) {
            log.error("Error while fetching feature flags: {}", e.getMessage(), e);
            return List.of();
        }
    }
}
