package com.featureflag.shared.exception;

public class FeatureFlagNotFoundException extends FeatureFlagException {
    public FeatureFlagNotFoundException(long id) {
        super("FeatureFlag not found: " + id);
    }

}
