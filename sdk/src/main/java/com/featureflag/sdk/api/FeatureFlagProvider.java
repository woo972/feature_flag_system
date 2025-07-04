package com.featureflag.sdk.api;

import com.featureflag.shared.model.FeatureFlag;
import java.util.List;

public interface FeatureFlagProvider {
    List<FeatureFlag> fetchAll();
}
