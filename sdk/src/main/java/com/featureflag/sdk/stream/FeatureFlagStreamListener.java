package com.featureflag.sdk.stream;

import java.util.function.*;

public interface FeatureFlagStreamListener extends AutoCloseable {
    void initialize(Consumer<String> onFeatureFlagUpdated);
    @Override
    void close();
}
