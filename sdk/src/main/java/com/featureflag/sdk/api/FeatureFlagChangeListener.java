package com.featureflag.sdk.api;

public interface FeatureFlagChangeListener extends AutoCloseable {
    void initialize();
    void onChange(String featureFlagName, boolean oldValue, boolean newValue);
    @Override
    void close();
}
