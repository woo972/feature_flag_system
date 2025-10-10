package com.featureflag.sdk.stream;

import java.util.function.LongConsumer;

public interface FeatureFlagStreamListener extends AutoCloseable {
    void initialize(LongConsumer onFeatureFlagUpdated);
    @Override
    void close();
}
