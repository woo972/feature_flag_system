package com.featureflag.sample;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.*;
import com.featureflag.sdk.*;
import com.featureflag.sdk.api.*;
import com.featureflag.sdk.config.*;
import org.springframework.context.annotation.*;

@Configuration
public class SampleFeatureFlagConfig {

    @Bean
    public FeatureFlagClient featureFlagClient() {
        return DefaultFeatureFlagClient.builder()
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

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }
}
