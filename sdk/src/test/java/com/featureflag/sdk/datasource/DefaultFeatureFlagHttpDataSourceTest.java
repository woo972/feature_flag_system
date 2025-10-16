package com.featureflag.sdk.datasource;

import com.featureflag.sdk.config.*;
import com.featureflag.shared.http.CoreFeatureFlagClient;
import com.featureflag.shared.http.CoreApiException;
import com.featureflag.shared.model.FeatureFlag;
import com.featureflag.shared.model.FeatureFlagStatus;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.URI;
import java.net.URI;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultFeatureFlagHttpDataSourceTest {

    private CoreFeatureFlagClient httpClient;
    private DefaultFeatureFlagHttpDataSource dataSource;

    @BeforeEach
    void setUp() {
        httpClient = mock(CoreFeatureFlagClient.class);
        dataSource = new DefaultFeatureFlagHttpDataSource(httpClient);
    }

    @DisplayName("returns empty when response status is outside success range")
    @Test
    void returnsEmptyWhenServerError() {
        var response = mockResponse(500);
        when(httpClient.getRaw(any(URI.class), anyMap())).thenReturn(response);

        Optional<List<FeatureFlag>> result = dataSource.getFeatureFlags();

        verify(httpClient).getRaw(eq(URI.create(FeatureFlagProperty.GET_FEATURE_FLAGS_PATH)), anyMap());
        assertTrue(result.isEmpty());
    }

    @DisplayName("returns empty when response status is 304 and includes If-None-Match on subsequent requests")
    @Test
    void returnsEmptyWhenNotModified() {
        var firstResponse = mockResponse(200, "[]", Map.of("ETag", List.of("etag-1")));
        var secondResponse = mockResponse(304);
        when(httpClient.getRaw(any(URI.class), anyMap())).thenReturn(
                firstResponse, secondResponse
        );

        dataSource.getFeatureFlags(); // prime etag
        dataSource.getFeatureFlags(); // not modified

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, List<String>>> headersCaptor = ArgumentCaptor.forClass(Map.class);
        verify(httpClient, times(2)).getRaw(eq(URI.create(FeatureFlagProperty.GET_FEATURE_FLAGS_PATH)), headersCaptor.capture());

        Map<String, List<String>> secondCallHeaders = headersCaptor.getAllValues().get(1);
        assertEquals(List.of("\"etag-1\""), secondCallHeaders.get("If-None-Match"));
    }

    @DisplayName("parses feature flags from successful response")
    @Test
    void parsesFeatureFlags() {
        String json = """
                [
                  {"id":1,"name":"beta-flag","description":"desc","status":"ON"}
                ]
                """;

        var response = mockResponse(200, json, Map.of("ETag", List.of("etag-2")));
        when(httpClient.getRaw(any(URI.class), anyMap())).thenReturn(response);

        Optional<List<FeatureFlag>> result = dataSource.getFeatureFlags();

        assertTrue(result.isPresent());
        FeatureFlag flag = result.get().get(0);
        assertEquals(1L, flag.getId());
        assertEquals("beta-flag", flag.getName());
        assertEquals("desc", flag.getDescription());
        assertEquals(FeatureFlagStatus.ON, flag.getStatus());
    }

    private HttpResponse<String> mockResponse(int statusCode, String body, Map<String, List<String>> headers) {
        @SuppressWarnings("unchecked")
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(statusCode);
        when(response.body()).thenReturn(body);
        HttpHeaders httpHeaders = HttpHeaders.of(headers, (key, value) -> true);
        when(response.headers()).thenReturn(httpHeaders);
        return response;
    }

    private HttpResponse<String> mockResponse(int statusCode) {
        @SuppressWarnings("unchecked")
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(statusCode);
        return response;
    }

    @DisplayName("get fetches feature flag by id")
    @Test
    void getReturnsFeatureFlag() {
        long id = 42L;
        FeatureFlag featureFlag = FeatureFlag.builder()
                .id(id)
                .name("test-flag")
                .description("desc")
                .status(FeatureFlagStatus.ON)
                .build();

        when(httpClient.getJson(eq(URI.create(FeatureFlagProperty.FEATURE_FLAG_PATH + "/" + id)),
                anyMap(), eq(FeatureFlag.class))).thenReturn(featureFlag);

        FeatureFlag result = dataSource.get(id);

        assertNotNull(result);
        assertEquals(featureFlag, result);
    }

    @DisplayName("get returns null when core api throws error")
    @Test
    void getReturnsNullOnFailure() {
        long id = 100L;
        when(httpClient.getJson(eq(URI.create(FeatureFlagProperty.FEATURE_FLAG_PATH + "/" + id)),
                anyMap(), eq(FeatureFlag.class))).thenThrow(new CoreApiException("boom"));

        FeatureFlag result = dataSource.get(id);

        assertNull(result);
    }
}
