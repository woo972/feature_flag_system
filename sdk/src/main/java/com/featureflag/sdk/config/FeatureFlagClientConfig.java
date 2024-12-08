package com.featureflag.sdk.config;

public class FeatureFlagClientConfig {
    private final String baseUrl;
    private final String apiKey;
    private final int connectTimeoutMs;
    private final int readTimeoutMs;
    private final int cacheSize;
    private final int cacheExpirationMinutes;

    private FeatureFlagClientConfig(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.apiKey = builder.apiKey;
        this.connectTimeoutMs = builder.connectTimeoutMs;
        this.readTimeoutMs = builder.readTimeoutMs;
        this.cacheSize = builder.cacheSize;
        this.cacheExpirationMinutes = builder.cacheExpirationMinutes;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public int getReadTimeoutMs() {
        return readTimeoutMs;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public int getCacheExpirationMinutes() {
        return cacheExpirationMinutes;
    }

    public static class Builder {
        private String baseUrl;
        private String apiKey;
        private int connectTimeoutMs = 5000;
        private int readTimeoutMs = 5000;
        private int cacheSize = 1000;
        private int cacheExpirationMinutes = 5;

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder connectTimeoutMs(int connectTimeoutMs) {
            this.connectTimeoutMs = connectTimeoutMs;
            return this;
        }

        public Builder readTimeoutMs(int readTimeoutMs) {
            this.readTimeoutMs = readTimeoutMs;
            return this;
        }

        public Builder cacheSize(int cacheSize) {
            this.cacheSize = cacheSize;
            return this;
        }

        public Builder cacheExpirationMinutes(int cacheExpirationMinutes) {
            this.cacheExpirationMinutes = cacheExpirationMinutes;
            return this;
        }

        public FeatureFlagClientConfig build() {
            if (baseUrl == null || baseUrl.isEmpty()) {
                throw new IllegalStateException("baseUrl must be set");
            }
            if (apiKey == null || apiKey.isEmpty()) {
                throw new IllegalStateException("apiKey must be set");
            }
            return new FeatureFlagClientConfig(this);
        }
    }
}
