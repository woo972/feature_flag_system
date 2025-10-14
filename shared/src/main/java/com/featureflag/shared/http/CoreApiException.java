package com.featureflag.shared.http;

public class CoreApiException extends RuntimeException {
    public CoreApiException(String message) {
        super(message);
    }

    public CoreApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
