package com.featureflag.sdk.config;

public class FeatureFlagProperty {
    public static final String BASE_URL = "http://localhost:8080";
    public static final String FEATURE_FLAG_PATH = BASE_URL +"/api/v1/feature-flags";
    public static final String GET_FEATURE_FLAGS_PATH = FEATURE_FLAG_PATH;
    public static final String FEATURE_FLAG_STREAM_PATH = FEATURE_FLAG_PATH +"/event-stream";
}
