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

class FeatureFlagProviderTest {

    FeatureFlagProvider sut;

    FeatureFlagCoreHttpClient featureFlagCoreHttpClient = Mockito.mock(FeatureFlagCoreHttpClient.class);
    HttpClient httpClient = Mockito.mock(HttpClient.class);
    HttpResponse httpResponse = Mockito.mock(HttpResponse.class);

    @BeforeEach
    void setUp() {
        sut = new FeatureFlagProvider(featureFlagCoreHttpClient);
    }

    @DisplayName("returns feature flags when fetchAll is called")
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
        var resutl = sut.fetchAll();
        assertEquals(1, resutl.size());
        assertEquals("dummy-flag-A", resutl.get(0).getName());
    }
}