package com.featureflag.sdk;

import com.featureflag.sdk.api.FeatureFlagCache;
import com.featureflag.sdk.api.FeatureFlagClient;
import com.featureflag.sdk.api.FeatureFlagProvider;
import com.featureflag.sdk.config.FeatureFlagCoreHttpClient;
import lombok.Builder;

@Builder
public class FeatureFlagFactory {

    private FeatureFlagCache cache;
    private FeatureFlagProvider provider;

    public FeatureFlagClient create() {
        if(cache == null){
            this.cache = new DefaultFeatureFlagLocalCache();
        }
        if(provider == null){
            this.provider = new DefaultFeatureFlagProvider(new FeatureFlagCoreHttpClient());
        }
        return DefaultFeatureFlagClient.builder()
                .cache(cache)
                .provider(provider)
                .build();
    }
}
