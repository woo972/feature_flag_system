package com.featureflag.core.apikey.domain.repository;

import com.featureflag.core.apikey.domain.model.ApiKey;
import com.featureflag.core.apikey.domain.model.ApiKeyId;
import com.featureflag.core.apikey.domain.model.ApiKeyValue;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository {
    ApiKey save(ApiKey apiKey);

    Optional<ApiKey> findById(ApiKeyId id);

    Optional<ApiKey> findByValue(ApiKeyValue apiKeyValue);

    List<ApiKey> findAll();

    void delete(ApiKeyId id);

    boolean existsById(ApiKeyId id);

    boolean existsByValue(ApiKeyValue apiKeyValue);
}
