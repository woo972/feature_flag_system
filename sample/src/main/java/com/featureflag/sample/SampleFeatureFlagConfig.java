package com.featureflag.sample;

import com.featureflag.sdk.api.*;
import org.springframework.context.annotation.*;

@Configuration
public class SampleFeatureFlagConfig {

    @Bean
    public FeatureFlagClient featureFlagClient() {
        return DefaultFeatureFlagClient.builder().build();
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
