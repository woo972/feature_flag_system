package com.featureflag.core.service;

import com.featureflag.core.repository.FeatureFlagRepository;
import com.featureflag.shared.model.FeatureFlag;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class FeatureFlagService {
    private final FeatureFlagRepository repository;

    public FeatureFlagService(FeatureFlagRepository repository) {
        this.repository = repository;
    }

    @Cacheable(value = "featureFlags", key = "#flagId")
    public boolean evaluateFlag(Long flagId, Map<String, String> criteria) {
        Optional<FeatureFlag> flag = repository.findById(flagId);
        
        if (flag.isEmpty() || flag.get().getStatus() == FeatureFlag.Status.OFF) {
            return false;
        }

        return evaluateCriteria(flag.get(), criteria);
    }

    @CacheEvict(value = "featureFlags", allEntries = true)
    public void refreshCache() {
        // Cache is cleared via annotation
    }

    private boolean evaluateCriteria(FeatureFlag flag, Map<String, String> criteria) {
        return flag.getCriteria().entrySet().stream()
            .allMatch(entry -> {
                String value = criteria.get(entry.getKey());
                return value != null && entry.getValue().contains(value);
            });
    }
}
