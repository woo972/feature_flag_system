package com.featureflag.sdk.datasource;

import com.featureflag.shared.model.FeatureFlag;

import java.util.List;
import java.util.Optional;

public interface FeatureFlagDataSource {
    FeatureFlag get(long featureFlagId);
    Optional<List<FeatureFlag>> getFeatureFlags();
}
