package com.featureflag.sdk.scheduler;

public interface FeatureFlagScheduler extends AutoCloseable {
    void initialize(Runnable runnable);
    @Override
    void close();
}
