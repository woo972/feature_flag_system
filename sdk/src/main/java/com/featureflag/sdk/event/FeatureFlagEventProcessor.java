package com.featureflag.sdk.event;

import java.util.Map;

public interface FeatureFlagEventProcessor extends AutoCloseable {
    void initialize();
    void recordEvaluation(long featureFlagId, Map<String, String> criteria, boolean isEnabled);
    void flush();
    @Override
    default void close(){
        flush();
    }
}
