package com.featureflag.sdk.config;

import lombok.extern.slf4j.*;

import java.io.*;
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

    public HttpResponse<InputStream> connectStream(String url, Map<String, List<String>> headers) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put("Accept", List.of("text/event-stream"));
        headers.put("Cache-Control", List.of("no-cache"));
        // java.lang.IllegalArgumentException: restricted header name: "Connection"
//        headers.put("Connection", List.of("keep-alive"));

        return this.get(url, headers, HttpResponse.BodyHandlers.ofInputStream());
    }

    public HttpResponse<String> get(String url, Map<String, List<String>> headers) {
        return get(url, headers, HttpResponse.BodyHandlers.ofString());
    }

    private <T> HttpResponse<T> get(String url, Map<String, List<String>> headers, HttpResponse.BodyHandler<T> bodyHandler) {
        int retryCount = 0;
        while (retryCount < MAX_RETRIES) {
            try {
                HttpRequest request = withHeaders(HttpRequest.newBuilder()
                                .uri(URI.create(url))
                                .GET()
                                .timeout(READ_TIMOUT),
                        headers
                ).build();

                return httpClient.send(request, bodyHandler);
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
