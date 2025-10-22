package com.featureflag.shared.exception;

public abstract class FeatureFlagException extends RuntimeException {

    protected FeatureFlagException(String message) {
        super(message);
    }

    protected FeatureFlagException(String message, Throwable cause) {
        super(message, cause);
    }
}
