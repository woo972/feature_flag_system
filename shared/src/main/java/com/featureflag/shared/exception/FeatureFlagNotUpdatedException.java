package com.featureflag.shared.exception;

public class FeatureFlagNotUpdatedException extends RuntimeException {
    public FeatureFlagNotUpdatedException(Long id) {
        super("FeatureFlag not updated: " + id);
    }

}
