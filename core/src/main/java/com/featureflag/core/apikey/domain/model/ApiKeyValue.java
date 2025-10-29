package com.featureflag.core.apikey.domain.model;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

public class ApiKeyValue {
    private static final int API_KEY_LENGTH = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final String value;

    private ApiKeyValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("API key value cannot be null or blank");
        }
        this.value = value;
    }

    public static ApiKeyValue of(String value) {
        return new ApiKeyValue(value);
    }

    public static ApiKeyValue generate() {
        byte[] randomBytes = new byte[API_KEY_LENGTH];
        SECURE_RANDOM.nextBytes(randomBytes);
        String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        return new ApiKeyValue(encoded);
    }

    public String getValue() {
        return value;
    }

    public String getMaskedValue() {
        if (value.length() < 8) {
            return "****";
        }
        return value.substring(0, 4) + "..." + value.substring(value.length() - 4);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApiKeyValue that = (ApiKeyValue) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return getMaskedValue();
    }
}
