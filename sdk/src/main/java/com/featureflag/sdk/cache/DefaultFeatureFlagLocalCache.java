package com.featureflag.sdk.cache;

import com.featureflag.shared.model.FeatureFlag;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.util.*;

@Getter
@Slf4j
public class DefaultFeatureFlagLocalCache implements FeatureFlagCache {

    private static final Cache<String, FeatureFlag> LOCAL_CACHE = Caffeine.newBuilder()
            .maximumSize(1000)
            .recordStats()
            .build();

    /**
     * 서버에서 피처 플래그 데이터를 로드하여 캐시에 저장합니다.
     */
    @Override
    public void load(Optional<List<FeatureFlag>> featureFlags) {
        featureFlags.ifPresent(flags ->
                flags.forEach(flag -> {
                    LOCAL_CACHE.put(flag.getKey(), flag);
                })
        );

        log.debug("Feature flag cache loaded. feature flag size: {}", featureFlags.map(List::size).orElse(0));
    }

    @Override
    public Optional<FeatureFlag> get(String key) {
        var featureFlag = LOCAL_CACHE.getIfPresent(key);
        if (featureFlag == null) {
            log.error("Feature flag local cache miss. Key: {}", key);
        }

        return Optional.ofNullable(featureFlag);
    }

    @Override
    public Optional<List<FeatureFlag>> readAll() {
        Map<String, FeatureFlag> featureFlags = LOCAL_CACHE.asMap();
        if (featureFlags == null) {
            log.error("Feature flags local cache is empty");
            return Optional.empty();
        }
        // convert to list
        List<FeatureFlag> featureFlagList = new ArrayList<>(featureFlags.values());
        return Optional.of(featureFlagList);
    }

    @Override
    public void put(String key, Optional<FeatureFlag> value) {
        value.ifPresent(v -> LOCAL_CACHE.put(key, v));
    }
}

