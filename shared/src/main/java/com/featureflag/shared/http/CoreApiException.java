package com.featureflag.shared.http;

import com.featureflag.shared.exception.FeatureFlagException;

public class CoreApiException extends FeatureFlagException {
    public CoreApiException(String message) {
        super(message);
    }

    public CoreApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
