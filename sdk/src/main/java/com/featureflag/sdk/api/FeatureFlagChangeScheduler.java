package com.featureflag.sdk.api;

public interface FeatureFlagChangeScheduler extends AutoCloseable {
    void initialize(Runnable runnable);
    @Override
    void close();
}
