package com.featureflag.sdk.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

public class FeatureFlagCoreHttpClient {
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    private static final Duration CONNECTION_TIMOUT = Duration.ofSeconds(3);
    private static final Duration READ_TIMOUT = Duration.ofSeconds(3);

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(CONNECTION_TIMOUT)
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public HttpClient get(){
        return httpClient;
    }

    public static final HttpRequest GET_FEATURE_FLAGS = HttpRequest.newBuilder()
            .uri(URI.create(FeatureFlagProperty.GET_FEATURE_FLAGS_PATH))
            .GET()
            .header("Content-Type", "application/json")
            .timeout(READ_TIMOUT)
            .build();


}
