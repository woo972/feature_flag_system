package com.featureflag.sdk;

import com.featureflag.sdk.config.FeatureFlagCoreHttpClient;
import com.featureflag.shared.model.FeatureFlag;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;

@Slf4j
@Builder
public class FeatureFlagProvider {

    private final FeatureFlagCoreHttpClient httpClient;
    private static final int MAX_RETRY = 3;

    public List<FeatureFlag> fetchAll() {
        for (int i = 0; i < MAX_RETRY; i++) {
            try {
                String response = httpClient.get().send(
                        FeatureFlagCoreHttpClient.GET_FEATURE_FLAGS,
                        HttpResponse.BodyHandlers.ofString()
                ).body();
                var parsedResponse = parseJsonToModel(response, FeatureFlag[].class);
                return convertArrayToList(parsedResponse);
            } catch (Exception e) {
                if (i == MAX_RETRY - 1) {
                    log.error(e.getMessage());
                }
            }
        }
        return emptyList();
    }


    private <T> List<T> convertArrayToList(T[] elements) {
        return elements != null ? Arrays.asList(elements) : emptyList();
    }

    private <T> T parseJsonToModel(String json, Class<T> clazz) throws Exception {
        return FeatureFlagCoreHttpClient.JSON_MAPPER.readValue(json, clazz);
    }
}
