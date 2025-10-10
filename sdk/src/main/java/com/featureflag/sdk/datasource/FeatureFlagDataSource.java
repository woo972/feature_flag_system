package com.featureflag.sdk.datasource;

import com.featureflag.shared.model.*;

import java.util.*;

public interface FeatureFlagDataSource {
    FeatureFlag get(long featureFlagId);
    Optional<List<FeatureFlag>> getFeatureFlags();
}
