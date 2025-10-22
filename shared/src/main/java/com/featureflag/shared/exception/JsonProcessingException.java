package com.featureflag.shared.exception;

public class JsonProcessingException extends FeatureFlagException {

    public JsonProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
