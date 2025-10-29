package com.featureflag.core.apikey.application.dto;

import com.featureflag.core.apikey.domain.model.ApiKey;
import com.featureflag.core.apikey.domain.model.ApiKeyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiKeyDto {
    private Long id;
    private String apiKey;
    private String name;
    private String description;
    private ApiKeyStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime lastUsedAt;
    private LocalDateTime expiresAt;
    private Boolean isActive;
    private Boolean isExpired;

    /**
     * Creates DTO from domain model with full API key value.
     */
    public static ApiKeyDto fromDomain(ApiKey apiKey, boolean includeFullKey) {
        return ApiKeyDto.builder()
                .id(apiKey.getId() != null ? apiKey.getId().getValue() : null)
                .apiKey(includeFullKey
                        ? apiKey.getApiKeyValue().getValue()
                        : apiKey.getApiKeyValue().getMaskedValue())
                .name(apiKey.getName())
                .description(apiKey.getDescription())
                .status(apiKey.getStatus())
                .createdAt(apiKey.getCreatedAt())
                .lastUsedAt(apiKey.getLastUsedAt())
                .expiresAt(apiKey.getExpiresAt())
                .isActive(apiKey.isActive())
                .isExpired(apiKey.isExpired())
                .build();
    }

    public static ApiKeyDto fromDomain(ApiKey apiKey) {
        return fromDomain(apiKey, false);
    }
}
