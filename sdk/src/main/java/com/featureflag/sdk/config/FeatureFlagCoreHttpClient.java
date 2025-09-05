package com.featureflag.sdk.config;

import com.fasterxml.jackson.core.type.*;
import lombok.extern.slf4j.*;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;

@Slf4j
public class FeatureFlagCoreHttpClient {
    public static final int MAX_RETRIES = 3;
    private static final Duration CONNECTION_TIMOUT = Duration.ofSeconds(3);
    private static final Duration READ_TIMOUT = Duration.ofSeconds(3);

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(CONNECTION_TIMOUT)
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public HttpResponse<String> get(String url, Map<String, List<String>> headers) {
        int retryCount = 0;
        while (retryCount < MAX_RETRIES) {
            try {
                HttpRequest request = withHeaders(HttpRequest.newBuilder()
                                .uri(URI.create(url))
                                .GET()
                                .timeout(READ_TIMOUT),
                        headers
                ).build();

                return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (Exception e) {
                log.warn("Http request failed. {} times. url: {}", retryCount + 1, url, e);
                retryCount++;
            }
        }
        return null;
    }

    private HttpRequest.Builder withHeaders(HttpRequest.Builder builder, Map<String, List<String>> headers) {
        if (headers != null) {
            headers.forEach((key, values) -> {
                if (values != null) {
                    values.forEach(value -> {
                        builder.header(key, value);
                    });
                }
            });
        }
        return builder;
    }
}
