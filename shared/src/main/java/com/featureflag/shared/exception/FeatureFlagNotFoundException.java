package com.featureflag.shared.exception;

public class FeatureFlagNotFoundException extends RuntimeException {
    public FeatureFlagNotFoundException(Long id) {
        super("FeatureFlag not found: " + id);
    }

}
