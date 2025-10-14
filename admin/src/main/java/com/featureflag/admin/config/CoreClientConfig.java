package com.featureflag.admin.config;

import java.net.http.HttpClient;
import java.time.Duration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.featureflag.shared.http.CoreFeatureFlagClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CoreApiProperties.class)
public class CoreClientConfig {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(REQUEST_TIMEOUT)
                .version(HttpClient.Version.HTTP_2)
                .build();
    }

    @Bean
    public CoreFeatureFlagClient coreFeatureFlagClient(HttpClient httpClient, ObjectMapper objectMapper) {
        return new CoreFeatureFlagClient(httpClient, objectMapper);
    }
}
