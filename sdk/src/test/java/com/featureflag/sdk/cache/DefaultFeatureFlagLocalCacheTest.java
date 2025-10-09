package com.featureflag.sdk.cache;

import com.featureflag.shared.model.FeatureFlag;
import com.featureflag.shared.model.FeatureFlagStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertSame;

class DefaultFeatureFlagLocalCacheTest {

    private DefaultFeatureFlagLocalCache cache;

    @BeforeEach
    void setUp() throws Exception {
        cache = new DefaultFeatureFlagLocalCache();
        cache.invalidateAll();
    }

    @Test
    @DisplayName("stores feature flags and get returns cached value")
    void loadStoresFlags() {
        var featureFlag = sampleFlag("flag-" + UUID.randomUUID());

        cache.load(Optional.of(List.of(featureFlag)));
        var cached = cache.get(featureFlag.getKey());

        assertTrue(cached.isPresent());
        assertSame(featureFlag, cached.orElseThrow());
    }

    @Test
    @DisplayName("returns empty optional for missing keys")
    void getReturnsEmptyWhenMissing() {
        var missing = cache.get("missing-" + UUID.randomUUID());
        assertTrue(missing.isEmpty());
    }

    @Test
    @DisplayName("inserts optional feature flag into cache")
    void putInsertsValue() {
        var featureFlag = sampleFlag("flag-" + UUID.randomUUID());

        cache.put(featureFlag.getKey(), Optional.of(featureFlag));
        var cached = cache.get(featureFlag.getKey());

        assertTrue(cached.isPresent());
        assertSame(featureFlag, cached.orElseThrow());
    }

    @Test
    @DisplayName("returns all cached feature flags")
    void readAllReturnsCachedFlags() {
        var flags = List.of(
                sampleFlag("flag-" + UUID.randomUUID()),
                sampleFlag("flag-" + UUID.randomUUID())
        );

        cache.load(Optional.of(flags));
        var allFlags = cache.readAll();

        assertTrue(allFlags.isPresent());
        var cachedFlags = allFlags.orElseGet(List::of);
        assertEquals(2, cachedFlags.size());
        assertTrue(cachedFlags.containsAll(flags));
    }

    private FeatureFlag sampleFlag(String name) {
        return FeatureFlag.builder()
                .id(1L)
                .name(name)
                .status(FeatureFlagStatus.ON)
                .build();
    }
}
