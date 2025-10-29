package com.featureflag.core.apikey.application.service;

import com.featureflag.core.apikey.application.dto.ApiKeyDto;
import com.featureflag.core.apikey.application.dto.CreateApiKeyCommand;
import com.featureflag.core.apikey.domain.model.ApiKey;
import com.featureflag.core.apikey.domain.model.ApiKeyId;
import com.featureflag.core.apikey.domain.model.ApiKeyValue;
import com.featureflag.core.apikey.domain.repository.ApiKeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiKeyApplicationService {
    private final ApiKeyRepository apiKeyRepository;

    @Transactional
    public ApiKeyDto createApiKey(CreateApiKeyCommand command) {
        ApiKey apiKey = ApiKey.create(
                command.getName(),
                command.getDescription(),
                command.getExpiresAt()
        );

        ApiKey savedApiKey = apiKeyRepository.save(apiKey);

        log.info("Created new API key: {} (ID: {})",
                savedApiKey.getName(),
                savedApiKey.getId() != null ? savedApiKey.getId().getValue() : "pending");

        return ApiKeyDto.fromDomain(savedApiKey, true);
    }

    @Transactional(readOnly = true)
    public List<ApiKeyDto> getAllApiKeys() {
        return apiKeyRepository.findAll().stream()
                .map(ApiKeyDto::fromDomain)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ApiKeyDto> getApiKeyById(Long id) {
        return apiKeyRepository.findById(ApiKeyId.of(id))
                .map(ApiKeyDto::fromDomain);
    }

    @Transactional
    public void revokeApiKey(Long id) {
        ApiKey apiKey = apiKeyRepository.findById(ApiKeyId.of(id))
                .orElseThrow(() -> new IllegalArgumentException("API key not found: " + id));

        apiKey.revoke();

        apiKeyRepository.save(apiKey);

        log.info("Revoked API key: {} (ID: {})", apiKey.getName(), id);
    }

    @Transactional
    public ApiKeyDto activateApiKey(Long id) {
        ApiKey apiKey = apiKeyRepository.findById(ApiKeyId.of(id))
                .orElseThrow(() -> new IllegalArgumentException("API key not found: " + id));

        apiKey.activate();

        ApiKey savedApiKey = apiKeyRepository.save(apiKey);

        log.info("Activated API key: {} (ID: {})", apiKey.getName(), id);

        return ApiKeyDto.fromDomain(savedApiKey);
    }

    @Transactional
    public void deleteApiKey(Long id) {
        ApiKeyId apiKeyId = ApiKeyId.of(id);

        if (!apiKeyRepository.existsById(apiKeyId)) {
            throw new IllegalArgumentException("API key not found: " + id);
        }

        apiKeyRepository.delete(apiKeyId);

        log.info("Deleted API key with ID: {}", id);
    }

    @Transactional
    public boolean validateAndRecordUsage(String apiKeyString) {
        ApiKeyValue apiKeyValue = ApiKeyValue.of(apiKeyString);

        Optional<ApiKey> apiKeyOpt = apiKeyRepository.findByValue(apiKeyValue);
        if (apiKeyOpt.isEmpty()) {
            return false;
        }

        ApiKey apiKey = apiKeyOpt.get();

        if (!apiKey.isValid()) {
            return false;
        }

        try {
            apiKey.recordUsage();
            apiKeyRepository.save(apiKey);
        } catch (RuntimeException e) {
            log.error("Failed to record API key usage for: {}", apiKey.getName(), e);
        }

        return true;
    }

    @Transactional(readOnly = true)
    public Optional<ApiKey> findByValue(String apiKeyString) {
        ApiKeyValue apiKeyValue = ApiKeyValue.of(apiKeyString);
        return apiKeyRepository.findByValue(apiKeyValue);
    }
}
