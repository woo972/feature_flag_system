package com.featureflag.sdk.api;

import java.util.*;

public interface FeatureFlagEventProcessor extends AutoCloseable {
    void initialize();
    void recordEvaluation(String featureFlagName, Map<String, String> criteria, boolean isEnabled);
    void flush();
    @Override
    default void close(){
        flush();
    }
}
