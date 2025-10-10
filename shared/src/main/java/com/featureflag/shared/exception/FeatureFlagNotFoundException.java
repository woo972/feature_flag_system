package com.featureflag.shared.exception;

public class FeatureFlagNotFoundException extends RuntimeException {
    public FeatureFlagNotFoundException(long id) {
        super("FeatureFlag not found: " + id);
    }

}
