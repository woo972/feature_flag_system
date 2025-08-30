package com.featureflag.sdk;

import com.featureflag.sdk.api.*;
import com.featureflag.shared.model.FeatureFlag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DefaultFeatureFlagLocalCacheTest {

    private DefaultFeatureFlagLocalCache sut;

    @BeforeEach
    void setUp() {
        sut = new DefaultFeatureFlagLocalCache();
        sut.invalidate();
    }

    @DisplayName("get feature flag from cache")
    @Test
    void getFeatureFlagFromCache() {
        String featureFlagName = "feature-1";

        sut.load(List.of(FeatureFlag.builder().name(featureFlagName).build()));

        assertTrue(sut.isInitialized());
        assertEquals(featureFlagName, sut.get(featureFlagName).get().getName());
    }

    @DisplayName("returns null when feature flag not from cache")
    @Test
    void returnsNullWhenFeatureFlagNotFromCache() {
        assertFalse(sut.get("feature-1").isPresent());
    }

    @DisplayName("invalidate cache")
    @Test
    void invalidateCache() {
        String featureFlagName = "feature-1";

        sut.load(List.of(FeatureFlag.builder().name(featureFlagName).build()));

        sut.invalidate();
        assertFalse(sut.isInitialized());
    }
}