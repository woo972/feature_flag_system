package com.featureflag.sample;

import com.featureflag.sdk.client.DefaultFeatureFlagClient;
import com.featureflag.sdk.client.FeatureFlagClient;
import com.featureflag.sdk.client.UpdateMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class SampleFeatureFlagConfig {

    @Bean
    public FeatureFlagClient featureFlagClient() {
        return DefaultFeatureFlagClient
                .builder()
                .updateMode(UpdateMode.STREAM)
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
