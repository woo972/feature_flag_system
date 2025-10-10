package com.featureflag.core.service;

import com.featureflag.core.entity.*;
import com.featureflag.core.event.*;
import com.featureflag.core.repository.*;
import com.featureflag.shared.exception.*;
import com.featureflag.shared.model.*;
import lombok.*;
import org.springframework.cache.annotation.*;
import org.springframework.context.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import java.time.*;

@RequiredArgsConstructor
@Service
public class FeatureFlagCommandService {
    private final FeatureFlagRepository repository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public void register(RegisterFeatureFlagRequest request) {
        FeatureFlagEntity entity = new FeatureFlagEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        repository.save(entity);


    }

    @CacheEvict(value = "featureFlags", allEntries = true)
    public void refreshCache() {
        // Cache is cleared via annotation
    }

    @CacheEvict(value = "featureFlags", key = "#id")
    @Transactional
    public FeatureFlag on(long id) {
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

    @CacheEvict(value = "featureFlags", key = "#id")
    @Transactional
    public FeatureFlag off(long id) {
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

    @CacheEvict(value = "featureFlags", key = "#id")
    @Transactional
    public FeatureFlag archive(long id) {
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
}
