package com.featureflag.sdk;

import com.featureflag.sdk.api.FeatureFlagClient;
import com.featureflag.shared.model.FeatureFlag;
import com.featureflag.shared.model.FeatureFlagStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class SimpleFeatureFlagClientTest {

    FeatureFlagClient sut;
    FeatureFlagProvider provider = Mockito.mock(FeatureFlagProvider.class);
    DefaultFeatureFlagLocalCache cache = Mockito.mock(DefaultFeatureFlagLocalCache.class);

    @BeforeEach
    public void setUp() {
        sut = SimpleFeatureFlagClient
                .builder()
                .cache(cache)
                .provider(provider)
                .build();
    }

    @DisplayName("invalidate & load cache when initialize is called")
    @Test
    void invalidateAndLoadCache() {
        sut.initialize();

        verify(cache).invalidate();
        verify(provider).fetchAll();
        verify(cache).load(Mockito.any());
    }

    @DisplayName("returns true when feature flag is enabled")
    @Test
    void returnsTrueWhenFeatureFlagIsEnabled() {
        String featureFlagName = "feature-1";
        when(cache.get(featureFlagName)).thenReturn(Optional.of(FeatureFlag.builder()
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
        when(cache.isInitialized()).thenReturn(isInitialized);
        when(cache.get(featureFlagName)).thenReturn(Optional.of(FeatureFlag.builder()
                .name(featureFlagName)
                .status(FeatureFlagStatus.OFF)
                .build()));

        assertFalse(sut.evaluate(featureFlagName, Map.of()));
    }

    @DisplayName("scheduled task should update cache periodically")
    @Test
    void scheduledTaskShouldUpdateCachePeriodically() throws Exception {
        // Given
        List<FeatureFlag> initialFlags = List.of(
                FeatureFlag.builder().name("flag1").status(FeatureFlagStatus.ON).build()
        );

        List<FeatureFlag> updatedFlags = List.of(
                FeatureFlag.builder().name("flag1").status(FeatureFlagStatus.OFF).build(),
                FeatureFlag.builder().name("flag2").status(FeatureFlagStatus.ON).build()
        );

        // First call returns initialFlags, second call returns updatedFlags
        when(provider.fetchAll())
                .thenReturn(initialFlags)
                .thenReturn(updatedFlags);

        SimpleFeatureFlagClient client = SimpleFeatureFlagClient.builder()
                .cache(cache)
                .provider(provider)
                .build();

        // When
        // Simulate first update (happens in constructor)
        client.update();

        // Then
        verify(provider, times(1)).fetchAll();
        verify(cache, times(1)).load(initialFlags);

        // When
        // Simulate scheduled update
        client.update();

        // Then
        verify(provider, times(2)).fetchAll();
        verify(cache, times(1)).load(updatedFlags);
    }
}