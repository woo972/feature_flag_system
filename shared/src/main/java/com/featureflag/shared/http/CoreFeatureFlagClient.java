package com.featureflag.shared.http;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.featureflag.shared.config.JacksonConfig;
import com.featureflag.shared.constants.HttpHeaderNames;
import com.featureflag.shared.constants.HttpHeaderValues;
import com.featureflag.shared.util.JsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CoreFeatureFlagClient {
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);
    private static final String API_KEY_HEADER = "X-API-Key";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public CoreFeatureFlagClient() {
        this(defaultHttpClient(), JacksonConfig.getObjectMapper(), null);
    }

    public CoreFeatureFlagClient(String apiKey) {
        this(defaultHttpClient(), JacksonConfig.getObjectMapper(), apiKey);
    }

    public CoreFeatureFlagClient(HttpClient httpClient, ObjectMapper objectMapper) {
        this(httpClient, objectMapper, null);
    }

    public CoreFeatureFlagClient(HttpClient httpClient, ObjectMapper objectMapper, String apiKey) {
        this.httpClient = Objects.requireNonNull(httpClient, "httpClient must not be null");
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
        this.apiKey = apiKey;
    }

    private static HttpClient defaultHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(REQUEST_TIMEOUT)
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public ObjectMapper mapper() {
        return objectMapper;
    }

    public <T> T getJson(URI uri, Map<String, String> headers, Class<T> responseType) {
        return getJson(uri, headers, objectMapper.constructType(responseType));
    }

    public <T> T getJson(URI uri, Map<String, String> headers, JavaType responseType) {
        HttpRequest request = requestBuilder(uri, toMultiValue(headers))
                .header(HttpHeaderNames.ACCEPT, HttpHeaderValues.APPLICATION_JSON)
                .GET()
                .build();

        HttpResponse<String> response = send(request, BodyHandlers.ofString());
        ensureSuccess(response);
        return readBody(response.body(), responseType);
    }

    public <T> T postJson(URI uri, Map<String, String> headers, Object payload, Class<T> responseType) {
        return postJson(uri, headers, payload, objectMapper.constructType(responseType));
    }

    public <T> T postJson(URI uri, Map<String, String> headers, Object payload, JavaType responseType) {
        String body = payload == null ? "" : serializePayload(payload);
        HttpRequest.Builder builder = requestBuilder(uri, toMultiValue(headers))
                .header(HttpHeaderNames.ACCEPT, HttpHeaderValues.APPLICATION_JSON)
                .header(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);

        HttpRequest request = builder
                .POST(payload == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = send(request, BodyHandlers.ofString());
        ensureSuccess(response);
        if (responseType.getRawClass() == Void.class || response.body() == null || response.body().isBlank()) {
            return null;
        }
        return readBody(response.body(), responseType);
    }

    public HttpResponse<String> getRaw(URI uri, Map<String, List<String>> headers) {
        HttpRequest request = requestBuilder(uri, headers)
                .GET()
                .build();
        return send(request, BodyHandlers.ofString());
    }

    public HttpResponse<InputStream> connectStream(URI uri, Map<String, String> headers) {
        Map<String, List<String>> merged = toMultiValue(headers);
        merged.computeIfAbsent(HttpHeaderNames.ACCEPT, key -> new ArrayList<>()).add(HttpHeaderValues.TEXT_EVENT_STREAM);
        merged.computeIfAbsent(HttpHeaderNames.CACHE_CONTROL, key -> new ArrayList<>()).add(HttpHeaderValues.NO_CACHE);

        HttpRequest request = requestBuilder(uri, merged)
                .GET()
                .build();
        return send(request, BodyHandlers.ofInputStream());
    }

    private HttpRequest.Builder requestBuilder(URI uri, Map<String, List<String>> headers) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri)
                .timeout(REQUEST_TIMEOUT);

        // Add API key header if configured
        if (apiKey != null && !apiKey.isBlank()) {
            builder.header(API_KEY_HEADER, apiKey);
        }

        if (headers != null) {
            headers.forEach((key, values) -> {
                if (values != null) {
                    values.forEach(value -> builder.header(key, value));
                }
            });
        }
        return builder;
    }

    private Map<String, List<String>> toMultiValue(Map<String, String> headers) {
        if (headers == null || headers.isEmpty()) {
            return Map.of();
        }
        Map<String, List<String>> multiValue = new HashMap<>();
        headers.forEach((key, value) -> {
            if (value != null) {
                multiValue.put(key, List.of(value));
            }
        });
        return multiValue;
    }

    private <T> HttpResponse<T> send(HttpRequest request, BodyHandler<T> handler) {
        try {
            return httpClient.send(request, handler);
        } catch (IOException e) {
            throw new CoreApiException("I/O error while calling Core API", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CoreApiException("Interrupted while calling Core API", e);
        }
    }

    private void ensureSuccess(HttpResponse<?> response) {
        int status = response.statusCode();
        if (status < 200 || status >= 300) {
            throw new CoreApiException("Core API responded with status %d".formatted(status));
        }
    }

    private <T> T readBody(String body, JavaType responseType) {
        if (body == null || body.isBlank()) {
            return null;
        }
        try {
            return JsonUtils.readValue(objectMapper, body, responseType);
        } catch (RuntimeException e) {
            throw new CoreApiException("Failed to parse Core API response", e);
        }
    }

    private String serializePayload(Object payload) {
        try {
            return JsonUtils.writeValue(objectMapper, payload);
        } catch (RuntimeException e) {
            throw new CoreApiException("Failed to serialize request body", e);
        }
    }
}
