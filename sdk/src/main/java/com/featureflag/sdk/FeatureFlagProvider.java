package com.featureflag.sdk;

import com.featureflag.sdk.config.FeatureFlagCoreHttpClient;
import com.featureflag.shared.model.FeatureFlag;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;

@Slf4j
@Builder
public class FeatureFlagProvider {

    private final FeatureFlagCoreHttpClient httpClient;

    public List<FeatureFlag> fetchAll() {
        String response;
        try {
            response =  httpClient.get().send(
                    FeatureFlagCoreHttpClient.GET_FEATURE_FLAGS,
                    HttpResponse.BodyHandlers.ofString()
            ).body();
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
            return emptyList();
        }

        var parsedResponse = parseJsonToModel(response, FeatureFlag[].class);
        return convertArrayToList(parsedResponse);
    }


    private <T> List<T> convertArrayToList(T[] elements) {
        return elements != null ? Arrays.asList(elements) : emptyList();
    }

    private <T> T parseJsonToModel(String json, Class<T> clazz) {
        try {
            return FeatureFlagCoreHttpClient.JSON_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
