package com.featureflag.sdk.datasource;

import com.featureflag.shared.model.*;

import java.util.*;

public interface FeatureFlagDataSource {
    FeatureFlag get(String featureFlagName);
    Optional<List<FeatureFlag>> getFeatureFlags();
}
