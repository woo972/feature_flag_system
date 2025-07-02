package com.featureflag.sdk;

import com.featureflag.sdk.config.FeatureFlagCoreHttpClient;
import com.featureflag.shared.model.FeatureFlag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class FeatureFlagClientServiceTest {

    FeatureFlagClientService sut;

    FeatureFlagCoreHttpClient featureFlagCoreHttpClient = Mockito.mock(FeatureFlagCoreHttpClient.class);
    HttpClient httpClient = Mockito.mock(HttpClient.class);
    HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
    FeatureFlagClientLocalCache cache = Mockito.mock(FeatureFlagClientLocalCache.class);

    @BeforeEach
    void setUp() {
        sut = new FeatureFlagClientService(featureFlagCoreHttpClient, cache);
    }

    @DisplayName("refresh cache when invoke initialize method")
    @Test
    void initialize() throws IOException, InterruptedException {
        when(featureFlagCoreHttpClient.get()).thenReturn(httpClient);
        when(httpClient.send(FeatureFlagCoreHttpClient.GET_FEATURE_FLAGS, HttpResponse.BodyHandlers.ofString()))
                .thenReturn(httpResponse);
        sut.initialize();
        Mockito.verify(cache).invalidate();
        Mockito.verify(cache).load(Mockito.anyList());
    }

    @DisplayName("returns false when cache miss and cache is not initialized")
    @Test
    public void returnsTrueIfCacheIsNotInitialized() {
        assertFalse(sut.evaluate("test-flag", null));
    }

    @DisplayName("returns false when cache miss and cache is initialized")
    @Test
    public void returnsTrueIfCacheIsInitialized() throws IOException, InterruptedException {
        when(featureFlagCoreHttpClient.get()).thenReturn(httpClient);
        when(httpClient.send(FeatureFlagCoreHttpClient.GET_FEATURE_FLAGS, HttpResponse.BodyHandlers.ofString()))
                .thenReturn(httpResponse);
        when(httpResponse.body())
                .thenReturn(FeatureFlagCoreHttpClient
                        .JSON_MAPPER
                        .writeValueAsString(List.of(FeatureFlag.builder()
                                .name("dummy-flag-A")
                                .build())));
        sut.initialize();
        assertFalse(sut.evaluate("dummy-flag-B", null));
    }
}