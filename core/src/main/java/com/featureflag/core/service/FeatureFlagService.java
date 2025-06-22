package com.featureflag.core.service;

import com.featureflag.core.entity.FeatureFlagEntity;
import com.featureflag.core.repository.FeatureFlagRepository;
import com.featureflag.shared.exception.FeatureFlagNotFoundException;
import com.featureflag.shared.model.FeatureFlag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FeatureFlagService {
    private final FeatureFlagRepository repository;

    @Transactional
    public void register(RegisterFeatureFlagRequest request) {
        FeatureFlagEntity entity = new FeatureFlagEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setCriteria(request.getCriteria());
        repository.save(entity);
    }

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

    @Transactional(readOnly = true)
    public FeatureFlag get(Long id) {
        return repository.findById(id)
                .map(FeatureFlagEntity::toDomainModel)
                .orElseThrow(() -> new FeatureFlagNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<FeatureFlag> list() {
        return repository.findAll()
                .stream()
                .map(FeatureFlagEntity::toDomainModel)
                .collect(Collectors.toList());
    }
}
