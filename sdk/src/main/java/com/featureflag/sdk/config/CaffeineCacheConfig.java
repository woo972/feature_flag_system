package com.featureflag.sdk.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class CaffeineCacheConfig {

    public static final Cache<String, String> LOCAL_CACHE = Caffeine.newBuilder()
            .maximumSize(1000)
            .build();
}
