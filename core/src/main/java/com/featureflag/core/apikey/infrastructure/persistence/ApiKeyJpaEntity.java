package com.featureflag.core.apikey.infrastructure.persistence;

import com.featureflag.core.apikey.domain.model.ApiKey;
import com.featureflag.core.apikey.domain.model.ApiKeyId;
import com.featureflag.core.apikey.domain.model.ApiKeyStatus;
import com.featureflag.core.apikey.domain.model.ApiKeyValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_keys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiKeyJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String apiKey;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApiKeyStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime lastUsedAt;

    @Column
    private LocalDateTime expiresAt;

    public ApiKey toDomain() {
        return ApiKey.builder()
                .id(id != null ? ApiKeyId.of(id) : null)
                .apiKeyValue(ApiKeyValue.of(apiKey))
                .name(name)
                .description(description)
                .status(status)
                .createdAt(createdAt)
                .lastUsedAt(lastUsedAt)
                .expiresAt(expiresAt)
                .build();
    }

    public static ApiKeyJpaEntity fromDomain(ApiKey apiKey) {
        return ApiKeyJpaEntity.builder()
                .id(apiKey.getId() != null ? apiKey.getId().getValue() : null)
                .apiKey(apiKey.getApiKeyValue().getValue())
                .name(apiKey.getName())
                .description(apiKey.getDescription())
                .status(apiKey.getStatus())
                .createdAt(apiKey.getCreatedAt())
                .lastUsedAt(apiKey.getLastUsedAt())
                .expiresAt(apiKey.getExpiresAt())
                .build();
    }

    public void updateFromDomain(ApiKey apiKey) {
        this.apiKey = apiKey.getApiKeyValue().getValue();
        this.name = apiKey.getName();
        this.description = apiKey.getDescription();
        this.status = apiKey.getStatus();
        this.createdAt = apiKey.getCreatedAt();
        this.lastUsedAt = apiKey.getLastUsedAt();
        this.expiresAt = apiKey.getExpiresAt();
    }
}
