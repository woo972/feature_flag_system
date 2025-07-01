package com.featureflag.sdk;

import com.featureflag.sdk.config.FeatureFlagConfig;
import com.featureflag.shared.model.FeatureFlag;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;

@Slf4j
public class FeatureFlagClientService {

    private final FeatureFlagClientLocalCache cache = new FeatureFlagClientLocalCache();

    public void initialize() {
        cache.invalidate();

        String response;
        try {
            response = FeatureFlagConfig.HTTP_CLIENT.send(
                    FeatureFlagConfig.GET_FEATURE_FLAGS,
                    HttpResponse.BodyHandlers.ofString()
            ).body();
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
            return;
        }

        var parsedResponse = parseJsonToModel(response, FeatureFlag[].class);
        var featureFlags = convertArrayToList(parsedResponse);

        cache.load(featureFlags);
    }


    private static <T> List<T> convertArrayToList(T[] elements) {
        return elements != null ? Arrays.asList(elements) : emptyList();
    }

    private <T> T parseJsonToModel(String json, Class<T> clazz) {
        try {
            return FeatureFlagConfig.JSON_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public boolean evaluate(String featureFlagName, Map<String, String> criteria) {
        return cache.get(featureFlagName)
                .map(featureFlag -> featureFlag.evaluate(criteria))
                .orElse(false);


    }
}
