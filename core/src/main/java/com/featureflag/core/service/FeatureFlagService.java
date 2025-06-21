package com.featureflag.core.service;

import com.featureflag.core.repository.FeatureFlagEntity;
import com.featureflag.core.repository.FeatureFlagRepository;
import com.featureflag.shared.model.FeatureFlag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FeatureFlagService {
    private final FeatureFlagRepository repository;

    @Cacheable(value = "featureFlags", key = "#flagId")
    public boolean evaluate(Long flagId, Map<String, String> criteria) {
        return repository.findById(flagId)
                .map(FeatureFlagEntity::toDomainModel)
                .map(flag -> flag.evaluate(criteria))
                .orElse(false);
    }

    @CacheEvict(value = "featureFlags", allEntries = true)
    public void refreshCache() {
        // Cache is cleared via annotation
    }
}
