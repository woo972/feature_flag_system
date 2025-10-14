package com.featureflag.admin.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.featureflag.shared.api.RegisterFeatureFlagRequest;
import com.featureflag.shared.http.CoreFeatureFlagClient;
import com.featureflag.shared.model.FeatureFlag;
import com.featureflag.shared.model.FeatureFlagStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import static org.assertj.core.api.Assertions.assertThat;

class CoreFeatureFlagClientTest {

    private HttpServer httpServer;
    private final Deque<TestResponse> responses = new ArrayDeque<>();
    private final List<CapturedRequest> capturedRequests = new ArrayList<>();
    private CoreFeatureFlagClient coreFeatureFlagClient;
    private ObjectMapper objectMapper;
    private String baseUrl;

    @BeforeEach
    void setUp() throws IOException {
        responses.clear();
        capturedRequests.clear();

        httpServer = HttpServer.create(new InetSocketAddress(0), 0);
        httpServer.createContext("/", new RecordingHandler());
        httpServer.start();

        int port = httpServer.getAddress().getPort();
        baseUrl = "http://localhost:" + port;

        objectMapper = com.featureflag.shared.config.JacksonConfig.getObjectMapper();

        HttpClient httpClient = HttpClient.newBuilder().build();
        coreFeatureFlagClient = new CoreFeatureFlagClient(httpClient, objectMapper);
    }

    @AfterEach
    void tearDown() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
        responses.clear();
        capturedRequests.clear();
    }

    @DisplayName("list returns page response from core service")
    @Test
    void listReturnsPage() {
        enqueueJsonResponse("""
                {
                  "content": [
                    {
                      "id": 1,
                      "name": "feature-1",
                      "description": "desc",
                      "status": "ON"
                    }
                  ],
                  "totalElements": 1,
                  "number": 0,
                  "size": 10,
                  "totalPages": 1,
                  "first": true,
                  "last": true
                }
                """);

        URI uri = URI.create(baseUrl + "/api/v1/feature-flags/page?page=0&size=10&sort=id,DESC");
        var pageType = coreFeatureFlagClient.mapper()
                .getTypeFactory()
                .constructParametricType(PageResponse.class, FeatureFlag.class);

        PageResponse<FeatureFlag> response = coreFeatureFlagClient.getJson(uri, Map.of(), pageType);

        CapturedRequest recorded = awaitSingleRequest();
        assertThat(recorded.method()).isEqualTo("GET");
        assertThat(recorded.path()).isEqualTo("/api/v1/feature-flags/page?page=0&size=10&sort=id,DESC");

        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent()).hasSize(1);
        FeatureFlag featureFlag = response.getContent().get(0);
        assertThat(featureFlag.getId()).isEqualTo(1L);
        assertThat(featureFlag.getStatus()).isEqualTo(FeatureFlagStatus.ON);
    }

    @DisplayName("on toggles flag using core API")
    @Test
    void onTogglesFlag() {
        enqueueJsonResponse("""
                {
                  "id": 1,
                  "name": "feature-1",
                  "description": "desc",
                  "status": "ON"
                }
                """);

        URI uri = URI.create(baseUrl + "/api/v1/feature-flags/1/on");
        FeatureFlag featureFlag = coreFeatureFlagClient.postJson(uri, Map.of(), null, FeatureFlag.class);

        CapturedRequest recorded = awaitSingleRequest();
        assertThat(recorded.method()).isEqualTo("POST");
        assertThat(recorded.path()).isEqualTo("/api/v1/feature-flags/1/on");
        assertThat(recorded.header("Content-Type")).isEqualTo("application/json");

        assertThat(featureFlag.getId()).isEqualTo(1L);
        assertThat(featureFlag.getStatus()).isEqualTo(FeatureFlagStatus.ON);
    }

    @DisplayName("register creates a feature flag")
    @Test
    void registerCreatesFlag() {
        enqueueJsonResponse("""
                {
                  "id": 10,
                  "name": "new-flag",
                  "description": "desc",
                  "status": "OFF"
                }
                """);

        RegisterFeatureFlagRequest request = RegisterFeatureFlagRequest.builder()
                .name("new-flag")
                .description("desc")
                .targetingRules(null)
                .build();

        URI uri = URI.create(baseUrl + "/api/v1/feature-flags");
        FeatureFlag featureFlag = coreFeatureFlagClient.postJson(uri, Map.of(), request, FeatureFlag.class);

        CapturedRequest recorded = awaitSingleRequest();
        assertThat(recorded.method()).isEqualTo("POST");
        assertThat(recorded.path()).isEqualTo("/api/v1/feature-flags");
        assertThat(recorded.header("Content-Type")).isEqualTo("application/json");
        assertThat(recorded.body()).isEqualTo(toJson(request));

        assertThat(featureFlag.getId()).isEqualTo(10L);
        assertThat(featureFlag.getStatus()).isEqualTo(FeatureFlagStatus.OFF);
    }

    private void enqueueJsonResponse(String body) {
        responses.add(new TestResponse(200, Map.of("Content-Type", "application/json"), body));
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private final class RecordingHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            CapturedRequest request = capture(exchange);
            capturedRequests.add(request);

            TestResponse response = responses.pollFirst();
            if (response == null) {
                exchange.sendResponseHeaders(500, -1);
                exchange.close();
                return;
            }

            Headers responseHeaders = exchange.getResponseHeaders();
            response.headers().forEach(responseHeaders::add);
            byte[] body = response.body().getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(response.status(), body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        }

        private CapturedRequest capture(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().toString();
            Map<String, List<String>> headers = new HashMap<>();
            exchange.getRequestHeaders().forEach((key, values) -> headers.put(key.toLowerCase(Locale.ROOT), List.copyOf(values)));
            byte[] bytes = exchange.getRequestBody().readAllBytes();
            String body = new String(bytes, StandardCharsets.UTF_8);
            return new CapturedRequest(method, path, headers, body);
        }
    }

    private record CapturedRequest(String method, String path, Map<String, List<String>> headers, String body) {
        String header(String name) {
            List<String> values = headers.get(name.toLowerCase(Locale.ROOT));
            return values == null || values.isEmpty() ? null : values.get(0);
        }
    }

    private record TestResponse(int status, Map<String, String> headers, String body) {
    }

    private CapturedRequest awaitSingleRequest() {
        long deadline = System.currentTimeMillis() + 1_000;
        while (capturedRequests.isEmpty() && System.currentTimeMillis() < deadline) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while waiting for request", e);
            }
        }
        assertThat(capturedRequests).hasSize(1);
        return capturedRequests.get(0);
    }
}
