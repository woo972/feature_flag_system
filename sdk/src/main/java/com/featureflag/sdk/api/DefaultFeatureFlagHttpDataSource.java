package com.featureflag.sdk.api;

import com.featureflag.sdk.config.*;
import com.featureflag.shared.model.*;

import java.io.*;
import java.net.http.*;
import java.util.*;

public class DefaultFeatureFlagHttpDataSource implements FeatureFlagDataSource {
    @Override
    public FeatureFlag get(String featureFlagName) {
        return null;
    }

    @Override
    public List<FeatureFlag> getAll() {
        // TODO: etag header 추가하여 비교
        try {
            var response = FeatureFlagCoreHttpClient.get().send(
                    FeatureFlagCoreHttpClient.GET_FEATURE_FLAGS,
                    HttpResponse.BodyHandlers.ofString()
            );
            if(response.statusCode() == 304){
                return List.of();
            }

            return List.of(FeatureFlagCoreHttpClient.JSON_MAPPER.readValue(response.body(), FeatureFlag[].class));
        } catch (IOException | InterruptedException e) {
            return List.of();
        }
    }
}
