package com.featureflag.sdk.scheduler;

public interface FeatureFlagChangeScheduler extends AutoCloseable {
    void initialize(Runnable runnable);
    @Override
    void close();
}
