package com.featureflag.sdk.cache;

import com.featureflag.shared.model.FeatureFlag;
import com.featureflag.shared.model.FeatureFlagStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertSame;

class DefaultFeatureFlagLocalCacheTest {
    private static final AtomicLong idSequence = new AtomicLong(1);
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
        var cached = cache.get(featureFlag.getId());

        assertTrue(cached.isPresent());
        assertSame(featureFlag, cached.orElseThrow());
    }

    @Test
    @DisplayName("returns empty optional for missing keys")
    void getReturnsEmptyWhenMissing() {
        var missing = cache.get(-999L);
        assertTrue(missing.isEmpty());
    }

    @Test
    @DisplayName("inserts optional feature flag into cache")
    void putInsertsValue() {
        var featureFlag = sampleFlag("flag-" + UUID.randomUUID());

        cache.put(featureFlag.getId(), Optional.of(featureFlag));
        var cached = cache.get(featureFlag.getId());

        assertTrue(cached.isPresent());
        assertSame(featureFlag, cached.orElseThrow());
    }

    @Test
    @DisplayName("removes entry when optional empty provided")
    void putRemovesValueWhenEmpty() {
        var featureFlag = sampleFlag("flag-" + UUID.randomUUID());
        cache.put(featureFlag.getId(), Optional.of(featureFlag));
        assertTrue(cache.get(featureFlag.getId()).isPresent());

        cache.put(featureFlag.getId(), Optional.empty());

        assertTrue(cache.get(featureFlag.getId()).isEmpty());
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
                .id(idSequence.getAndIncrement())
                .name(name)
                .status(FeatureFlagStatus.ON)
                .build();
    }
}
