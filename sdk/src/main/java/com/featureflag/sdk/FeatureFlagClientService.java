package com.featureflag.sdk;

import com.featureflag.sdk.config.FeatureFlagConfig;
import com.featureflag.shared.model.FeatureFlag;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;

@Slf4j
public class FeatureFlagClientService {

    public List<FeatureFlag> getFeatureFlags() {
        String response;
        try {
            response = FeatureFlagConfig.HTTP_CLIENT.send(
                    FeatureFlagConfig.GET_FEATURE_FLAGS,
                    HttpResponse.BodyHandlers.ofString()
            ).body();
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
            return emptyList();
        }

        var parsedResponse = parse(response, FeatureFlag[].class);
        return convertToList(parsedResponse);
    }

    private static List<FeatureFlag> convertToList(FeatureFlag[] parsedResponse) {
        return parsedResponse != null ? Arrays.asList(parsedResponse) : emptyList();
    }

    private <T> T parse(String json, Class<T> clazz) {
        try {
            return FeatureFlagConfig.JSON_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
