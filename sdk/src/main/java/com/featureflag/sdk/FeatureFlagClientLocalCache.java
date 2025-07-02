package com.featureflag.sdk;

import com.featureflag.shared.model.FeatureFlag;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Optional;

@Slf4j
public class FeatureFlagClientLocalCache {

    private boolean initialized = false;

    private static final Cache<String, FeatureFlag> LOCAL_CACHE = Caffeine.newBuilder()
            .maximumSize(1000)
            .recordStats()
            .build();

    public boolean isInitialized() {
        return initialized;
    }

    /**
     * 모든 캐시 데이터를 무효화합니다.
     */
    public void invalidate() {
        LOCAL_CACHE.invalidateAll();
    }

    /**
     * 서버에서 피처 플래그 데이터를 로드하여 캐시에 저장합니다.
     */
    public void load(List<FeatureFlag> featureFlags) {
        if (featureFlags == null || featureFlags.isEmpty()) {
            return;
        }

        featureFlags.forEach(featureFlag -> {
            LOCAL_CACHE.put(featureFlag.getName(), featureFlag);
        });

        this.initialized = true;
    }

    public Optional<FeatureFlag> get(String key) {
        var featureFlag = LOCAL_CACHE.getIfPresent(key);
        if (featureFlag == null) {
            log.error("Feature flag local cache miss. Key: {}", key);
        }

        return Optional.ofNullable(featureFlag);
    }
}

