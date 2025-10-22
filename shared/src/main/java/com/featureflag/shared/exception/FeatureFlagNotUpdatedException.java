package com.featureflag.shared.exception;

public class FeatureFlagNotUpdatedException extends FeatureFlagException {
    public FeatureFlagNotUpdatedException(long id) {
        super("FeatureFlag not updated: " + id);
    }

}
