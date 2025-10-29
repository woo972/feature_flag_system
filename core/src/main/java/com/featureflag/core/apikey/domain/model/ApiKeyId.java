package com.featureflag.core.apikey.domain.model;

import java.util.Objects;

public class ApiKeyId {
    private final Long value;

    private ApiKeyId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("API key ID must be a positive number");
        }
        this.value = value;
    }

    public static ApiKeyId of(Long value) {
        return new ApiKeyId(value);
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApiKeyId apiKeyId = (ApiKeyId) o;
        return Objects.equals(value, apiKeyId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
