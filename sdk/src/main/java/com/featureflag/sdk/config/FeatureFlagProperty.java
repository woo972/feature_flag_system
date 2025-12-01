package com.featureflag.sdk.config;

public class FeatureFlagProperty {
    public static final String BASE_URL = System.getenv("FEATURE_FLAG_BASE_URL") != null
            ? System.getenv("FEATURE_FLAG_BASE_URL")
            : (System.getProperty("feature.flag.base-url") != null
                    ? System.getProperty("feature.flag.base-url")
                    : "http://localhost:8082");
    public static final String FEATURE_FLAG_PATH = BASE_URL + "/api/v1/feature-flags";
    public static final String GET_FEATURE_FLAGS_PATH = FEATURE_FLAG_PATH;
    public static final String FEATURE_FLAG_STREAM_PATH = FEATURE_FLAG_PATH + "/event-stream";

    // API Key for authentication with Core module
    // This should be set via configuration or environment variable in production
    public static String API_KEY = null;
}
