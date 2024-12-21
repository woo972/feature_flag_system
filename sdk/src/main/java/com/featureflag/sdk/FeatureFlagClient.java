package com.featureflag.sdk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.featureflag.shared.config.JacksonConfig;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.featureflag.sdk.config.FeatureFlagClientConfig;
import com.featureflag.shared.model.FeatureFlag;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class FeatureFlagClient implements AutoCloseable {
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Cache<Long, FeatureFlag> flagCache;
    private final FeatureFlagClientConfig config;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> refreshTask;

    public FeatureFlagClient(FeatureFlagClientConfig config) {
        this.config = config;
        this.httpClient = HttpClients.custom()
                .build();
        this.objectMapper = JacksonConfig.getObjectMapper();
        this.flagCache = Caffeine.newBuilder()
                .maximumSize(config.getCacheSize())
                .expireAfterWrite(Duration.ofMinutes(config.getCacheExpirationMinutes()))
                .build();
        
        this.scheduler = new ScheduledThreadPoolExecutor(1);
        // Schedule periodic cache refresh
        this.refreshTask = scheduler.scheduleAtFixedRate(
            this::refreshCache,
            0, // Start immediately
            config.getCacheExpirationMinutes(), 
            TimeUnit.MINUTES
        );
    }

    public boolean isEnabled(Long flagId, Map<String, String> criteria) {
        FeatureFlag flag = flagCache.getIfPresent(flagId);
        if (flag == null) {
            refreshCache(); // Refresh cache if flag not found
            flag = flagCache.getIfPresent(flagId);
        }

        if (flag == null || flag.getStatus() == FeatureFlag.Status.OFF) {
            return false;
        }

        return evaluateCriteria(flag, criteria);
    }

    private boolean evaluateCriteria(FeatureFlag flag, Map<String, String> criteria) {
        return flag.getCriteria().entrySet().stream()
            .allMatch(entry -> {
                String value = criteria.get(entry.getKey());
                return value != null && entry.getValue().contains(value);
            });
    }

    public void refreshCache() {
        try {
            URI uri = new URIBuilder(config.getBaseUrl() + "/api/v1/flags").build();
            HttpGet request = new HttpGet(uri);
            request.addHeader("X-API-Key", config.getApiKey());

            httpClient.execute(request, response -> {
                String json = EntityUtils.toString(response.getEntity());
                List<FeatureFlag> flags = objectMapper.readValue(json, new TypeReference<List<FeatureFlag>>() {});
                flags.forEach(flag -> flagCache.put(flag.getId(), flag));
                return null;
            });
        } catch (Exception e) {
            // Log error but don't clear cache to maintain resilience
        }
    }

    @Override
    public void close() throws Exception {
        if (refreshTask != null) {
            refreshTask.cancel(true);
        }
        scheduler.shutdown();
        httpClient.close();
    }
}
