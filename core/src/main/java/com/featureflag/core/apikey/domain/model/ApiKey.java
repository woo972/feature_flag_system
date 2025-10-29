package com.featureflag.core.apikey.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing an API key.
 * This is a rich domain model containing business logic and invariants.
 */
public class ApiKey {
    private final ApiKeyId id;
    private final ApiKeyValue apiKeyValue;
    private final String name;
    private final String description;
    private ApiKeyStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime lastUsedAt;
    private final LocalDateTime expiresAt;

    private ApiKey(Builder builder) {
        if (builder.name == null || builder.name.isBlank()) {
            throw new IllegalArgumentException("API key name cannot be null or blank");
        }
        if (builder.name.length() > 100) {
            throw new IllegalArgumentException("API key name cannot exceed 100 characters");
        }
        if (builder.description != null && builder.description.length() > 255) {
            throw new IllegalArgumentException("API key description cannot exceed 255 characters");
        }

        this.id = builder.id;
        this.apiKeyValue = Objects.requireNonNull(builder.apiKeyValue, "API key value cannot be null");
        this.name = builder.name;
        this.description = builder.description;
        this.status = builder.status != null ? builder.status : ApiKeyStatus.ACTIVE;
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
        this.lastUsedAt = builder.lastUsedAt;
        this.expiresAt = builder.expiresAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static ApiKey create(String name, String description, LocalDateTime expiresAt) {
        return builder()
                .apiKeyValue(ApiKeyValue.generate())
                .name(name)
                .description(description)
                .expiresAt(expiresAt)
                .build();
    }

    public boolean isValid() {
        if (status != ApiKeyStatus.ACTIVE) {
            return false;
        }

        if (expiresAt != null && LocalDateTime.now().isAfter(expiresAt)) {
            return false;
        }

        return true;
    }

    public void revoke() {
        if (status == ApiKeyStatus.REVOKED) {
            throw new IllegalStateException("API key is already revoked");
        }
        this.status = ApiKeyStatus.REVOKED;
    }

    public void activate() {
        if (status == ApiKeyStatus.ACTIVE) {
            throw new IllegalStateException("API key is already active");
        }
        this.status = ApiKeyStatus.ACTIVE;
    }

    public void recordUsage() {
        if (!isValid()) {
            throw new IllegalStateException("Cannot record usage for invalid API key");
        }
        this.lastUsedAt = LocalDateTime.now();
    }

    /**
     * Checks if this API key has expired.
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Checks if this API key is active (not revoked).
     */
    public boolean isActive() {
        return status == ApiKeyStatus.ACTIVE;
    }

    // Getters
    public ApiKeyId getId() {
        return id;
    }

    public ApiKeyValue getApiKeyValue() {
        return apiKeyValue;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ApiKeyStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastUsedAt() {
        return lastUsedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApiKey apiKey = (ApiKey) o;
        return Objects.equals(id, apiKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class Builder {
        private ApiKeyId id;
        private ApiKeyValue apiKeyValue;
        private String name;
        private String description;
        private ApiKeyStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime lastUsedAt;
        private LocalDateTime expiresAt;

        public Builder id(ApiKeyId id) {
            this.id = id;
            return this;
        }

        public Builder apiKeyValue(ApiKeyValue apiKeyValue) {
            this.apiKeyValue = apiKeyValue;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder status(ApiKeyStatus status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder lastUsedAt(LocalDateTime lastUsedAt) {
            this.lastUsedAt = lastUsedAt;
            return this;
        }

        public Builder expiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public ApiKey build() {
            return new ApiKey(this);
        }
    }
}
