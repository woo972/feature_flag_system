package com.featureflag.sdk.api;

public interface FeatureFlagChangeListener extends AutoCloseable {
    void initialize();
    @Override
    void close();
}
