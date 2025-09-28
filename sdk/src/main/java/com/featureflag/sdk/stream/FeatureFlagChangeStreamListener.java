package com.featureflag.sdk.stream;

import java.util.function.*;

public interface FeatureFlagChangeStreamListener extends AutoCloseable {
    void initialize(Consumer<String> onFeatureFlagUpdated);
    @Override
    void close();
}
