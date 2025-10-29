package com.featureflag.core.apikey.infrastructure.persistence;

import com.featureflag.core.apikey.domain.model.ApiKey;
import com.featureflag.core.apikey.domain.model.ApiKeyId;
import com.featureflag.core.apikey.domain.model.ApiKeyValue;
import com.featureflag.core.apikey.domain.repository.ApiKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ApiKeyRepositoryImpl implements ApiKeyRepository {
    private final ApiKeyJpaRepository jpaRepository;

    @Override
    public ApiKey save(ApiKey apiKey) {
        ApiKeyJpaEntity entity;

        if (apiKey.getId() != null) {
            entity = jpaRepository.findById(apiKey.getId().getValue())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "API key not found: " + apiKey.getId().getValue()));
            entity.updateFromDomain(apiKey);
        } else {
            entity = ApiKeyJpaEntity.fromDomain(apiKey);
        }

        ApiKeyJpaEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<ApiKey> findById(ApiKeyId id) {
        return jpaRepository.findById(id.getValue())
                .map(ApiKeyJpaEntity::toDomain);
    }

    @Override
    public Optional<ApiKey> findByValue(ApiKeyValue apiKeyValue) {
        return jpaRepository.findByApiKey(apiKeyValue.getValue())
                .map(ApiKeyJpaEntity::toDomain);
    }

    @Override
    public List<ApiKey> findAll() {
        return jpaRepository.findAll().stream()
                .map(ApiKeyJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(ApiKeyId id) {
        jpaRepository.deleteById(id.getValue());
    }

    @Override
    public boolean existsById(ApiKeyId id) {
        return jpaRepository.existsById(id.getValue());
    }

    @Override
    public boolean existsByValue(ApiKeyValue apiKeyValue) {
        return jpaRepository.existsByApiKey(apiKeyValue.getValue());
    }
}
