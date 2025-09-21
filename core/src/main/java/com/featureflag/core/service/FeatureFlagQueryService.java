package com.featureflag.core.service;

import com.featureflag.core.entity.FeatureFlagEntity;
import com.featureflag.core.repository.FeatureFlagRepository;
import com.featureflag.shared.exception.FeatureFlagNotFoundException;
import com.featureflag.shared.model.FeatureFlag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;

@RequiredArgsConstructor
@Service
public class FeatureFlagQueryService {
    private final FeatureFlagRepository repository;
    private final ApplicationEventPublisher publisher;

    @Cacheable(value = "featureFlags", key = "#flagId")
    public boolean evaluate(Long flagId, Map<String, String> criteria) {
        return repository.findById(flagId)
                .map(FeatureFlagEntity::toDomainModel)
                .map(flag -> flag.evaluate(criteria))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public FeatureFlag get(Long id) {
        return repository.findById(id)
                .map(FeatureFlagEntity::toDomainModel)
                .orElseThrow(() -> new FeatureFlagNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Page<FeatureFlag> list(Pageable pageable) {
        return repository.findAll(pageable)
                .map(FeatureFlagEntity::toDomainModel);
    }

    @Transactional(readOnly = true)
    public List<FeatureFlag> findAll() {
        return repository.findAll()
                .stream()
                .map(FeatureFlagEntity::toDomainModel)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getLastModifiedEpochTime() {
        return repository.findTopByOrderByUpdatedAtDesc()
                .map(FeatureFlagEntity::getUpdatedAt)
                .map(localDateTime -> localDateTime.toEpochSecond(ZoneOffset.UTC))
                .orElse(0L);
    }
}
