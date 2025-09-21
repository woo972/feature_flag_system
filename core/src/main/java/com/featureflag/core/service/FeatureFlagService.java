package com.featureflag.core.service;

import com.featureflag.core.entity.FeatureFlagEntity;
import com.featureflag.core.event.FeatureFlagUpdatedEvent;
import com.featureflag.core.repository.FeatureFlagRepository;
import com.featureflag.shared.exception.FeatureFlagNotFoundException;
import com.featureflag.shared.exception.FeatureFlagNotUpdatedException;
import com.featureflag.shared.model.FeatureFlag;
import com.featureflag.shared.model.FeatureFlagStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;

import static com.featureflag.core.CacheConstants.CACHE_PREFIX;

@RequiredArgsConstructor
@Service
public class FeatureFlagService {
    private final FeatureFlagRepository repository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public void register(RegisterFeatureFlagRequest request) {
        FeatureFlagEntity entity = new FeatureFlagEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
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


    @Transactional
    public FeatureFlag on(Long id) {
        FeatureFlagEntity entity = repository.findById(id)
                .orElseThrow(() -> new FeatureFlagNotFoundException(id));

        if (FeatureFlagStatus.ON.equals(entity.getStatus())) {
            throw new FeatureFlagNotUpdatedException(id);
        }

        entity.setStatus(FeatureFlagStatus.ON);
        var featureFlag = entity.toDomainModel();
        publisher.publishEvent(new FeatureFlagUpdatedEvent(featureFlag));
        return featureFlag;
    }

    @Transactional
    public FeatureFlag off(Long id) {
        FeatureFlagEntity entity = repository.findById(id)
                .orElseThrow(() -> new FeatureFlagNotFoundException(id));

        if (FeatureFlagStatus.OFF.equals(entity.getStatus())) {
            throw new FeatureFlagNotUpdatedException(id);
        }

        entity.setStatus(FeatureFlagStatus.OFF);
        var featureFlag = entity.toDomainModel();
        publisher.publishEvent(new FeatureFlagUpdatedEvent(featureFlag));
        return featureFlag;
    }

    @Transactional
    public FeatureFlag archive(Long id) {
        FeatureFlagEntity entity = repository.findById(id)
                .orElseThrow(() -> new FeatureFlagNotFoundException(id));

        if (entity.getArchivedAt() != null) {
            throw new FeatureFlagNotUpdatedException(id);
        }

        entity.setArchivedAt(LocalDateTime.now());
        var featureFlag = entity.toDomainModel();
        publisher.publishEvent(new FeatureFlagUpdatedEvent(featureFlag));
        return featureFlag;
    }

    @Transactional(readOnly = true)
    public long getLastModifiedEpochTime() {
        return repository.findTopByOrderByUpdatedAtDesc()
                .map(FeatureFlagEntity::getUpdatedAt)
                .map(localDateTime -> localDateTime.toEpochSecond(ZoneOffset.UTC))
                .orElse(0L);
    }
}
