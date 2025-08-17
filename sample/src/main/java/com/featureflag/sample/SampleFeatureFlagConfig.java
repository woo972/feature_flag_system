package com.featureflag.sample;

import com.featureflag.sdk.*;
import com.featureflag.sdk.api.*;
import com.featureflag.sdk.config.*;
import lombok.*;
import org.springframework.context.annotation.*;

@Configuration
public class SampleFeatureFlagConfig {

    @Bean
    public FeatureFlagClient featureFlagClient() {
        return SimpleFeatureFlagClient.builder()
                .cache(new DefaultFeatureFlagLocalCache())
                .provider(FeatureFlagProvider.builder()
                        .httpClient(new FeatureFlagCoreHttpClient())
                        .build())
                .build();
    }

    @Bean
    @DependsOn("featureFlagClient")
    public FeatureFlagInitializer featureFlagInitializer() {
        return new FeatureFlagInitializer(featureFlagClient());
    }

    private static class FeatureFlagInitializer {
        private final FeatureFlagClient featureFlagClient;

        public FeatureFlagInitializer(FeatureFlagClient featureFlagClient) {
            this.featureFlagClient = featureFlagClient;
            this.featureFlagClient.initialize();
        }
    }
}
