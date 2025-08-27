package com.featureflag.sdk.api;

import com.featureflag.shared.model.*;

import java.util.*;

public interface FeatureFlagDataSource {
    FeatureFlag get(String featureFlagName);
    List<FeatureFlag> getAll();
}
