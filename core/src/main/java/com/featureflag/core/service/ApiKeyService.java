package com.featureflag.core.service;

import com.featureflag.core.entity.ApiKeyEntity;
import com.featureflag.core.repository.ApiKeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiKeyService {
    private static final int API_KEY_LENGTH = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private final ApiKeyRepository apiKeyRepository;

    @Transactional
    public ApiKeyEntity createApiKey(String name, String description, LocalDateTime expiresAt) {
        String apiKey = generateApiKey();

        ApiKeyEntity entity = ApiKeyEntity.builder()
                .apiKey(apiKey)
                .name(name)
                .description(description)
                .active(true)
                .createdAt(LocalDateTime.now())
                .expiresAt(expiresAt)
                .build();

        ApiKeyEntity saved = apiKeyRepository.save(entity);
        if (log.isInfoEnabled()) {
            log.info("Created new API key: {} (ID: {})", name, saved.getId());
        }
        return saved;
    }

    @Transactional(readOnly = true)
    public List<ApiKeyEntity> getAllApiKeys() {
        return apiKeyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<ApiKeyEntity> getApiKeyById(Long id) {
        return apiKeyRepository.findById(id);
    }

    @Transactional
    public void revokeApiKey(Long id) {
        ApiKeyEntity entity = apiKeyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("API key not found: " + id));

        entity.setActive(false);
        apiKeyRepository.save(entity);
        if (log.isInfoEnabled()) {
            log.info("Revoked API key: {} (ID: {})", entity.getName(), id);
        }
    }

    @Transactional
    public void deleteApiKey(Long id) {
        if (!apiKeyRepository.existsById(id)) {
            throw new IllegalArgumentException("API key not found: " + id);
        }
        apiKeyRepository.deleteById(id);
        if (log.isInfoEnabled()) {
            log.info("Deleted API key with ID: {}", id);
        }
    }

    @Transactional
    public ApiKeyEntity activateApiKey(Long id) {
        ApiKeyEntity entity = apiKeyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("API key not found: " + id));

        entity.setActive(true);
        ApiKeyEntity saved = apiKeyRepository.save(entity);
        if (log.isInfoEnabled()) {
            log.info("Activated API key: {} (ID: {})", entity.getName(), id);
        }
        return saved;
    }

    private String generateApiKey() {
        byte[] randomBytes = new byte[API_KEY_LENGTH];
        SECURE_RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    @Transactional(readOnly = true)
    public boolean validateApiKey(String apiKey) {
        Optional<ApiKeyEntity> entity = apiKeyRepository.findByApiKey(apiKey);
        if (entity.isEmpty()) {
            return false;
        }

        ApiKeyEntity key = entity.get();
        if (!key.getActive()) {
            return false;
        }

        return !(key.getExpiresAt() != null && LocalDateTime.now().isAfter(key.getExpiresAt()));
    }
}
