package com.featureflag.sdk;

import com.featureflag.sdk.api.FeatureFlagClient;
import com.featureflag.sdk.api.FeatureFlagProvider;
import com.featureflag.shared.model.FeatureFlag;
import com.featureflag.shared.model.FeatureFlagStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultFeatureFlagClientTest {

    FeatureFlagClient sut;
    FeatureFlagProvider provider = Mockito.mock(FeatureFlagProvider.class);
    DefaultFeatureFlagLocalCache cache = Mockito.mock(DefaultFeatureFlagLocalCache.class);

    @BeforeEach
    public void setUp() {
        sut = DefaultFeatureFlagClient
                .builder()
                .cache(cache)
                .provider(provider)
                .build();
    }

    @DisplayName("invalidate & load cache when initialize is called")
    @Test
    void invalidateAndLoadCache() {
        sut.initialize();

        Mockito.verify(cache).invalidate();
        Mockito.verify(provider).fetchAll();
        Mockito.verify(cache).load(Mockito.any());
    }

    @DisplayName("returns true when feature flag is enabled")
    @Test
    void returnsTrueWhenFeatureFlagIsEnabled() {
        String featureFlagName = "feature-1";
        Mockito.when(cache.get(featureFlagName)).thenReturn(Optional.of(FeatureFlag.builder()
                .name(featureFlagName)
                .status(FeatureFlagStatus.ON)
                .build()));

        assertTrue(sut.evaluate(featureFlagName, Map.of()));
    }

    @DisplayName("returns false when feature flag is disabled regardless of cache state")
    @ParameterizedTest
    @CsvSource({"true", "false"})
    void returnsFalseWhenFeatureFlagIsDisabled(boolean isInitialized) {
        String featureFlagName = "feature-1";
        Mockito.when(cache.isInitialized()).thenReturn(isInitialized);
        Mockito.when(cache.get(featureFlagName)).thenReturn(Optional.of(FeatureFlag.builder()
                .name(featureFlagName)
                .status(FeatureFlagStatus.OFF)
                .build()));

        assertFalse(sut.evaluate(featureFlagName, Map.of()));
    }
}