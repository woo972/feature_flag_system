package com.featureflag.core.featureflag.domain.event;

import com.featureflag.core.featureflag.domain.model.FeatureFlag;

public record FeatureFlagUpdatedEvent(FeatureFlag featureFlag) {
}
