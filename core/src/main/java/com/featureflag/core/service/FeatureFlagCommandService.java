package com.featureflag.core.service;

import com.featureflag.core.entity.FeatureFlagEntity;
import com.featureflag.core.entity.TargetingRuleEntity;
import com.featureflag.core.event.FeatureFlagUpdatedEvent;
import com.featureflag.core.repository.FeatureFlagRepository;
import com.featureflag.shared.api.RegisterFeatureFlagRequest;
import com.featureflag.shared.exception.FeatureFlagNotFoundException;
import com.featureflag.shared.exception.FeatureFlagNotUpdatedException;
import com.featureflag.shared.model.FeatureFlag;
import com.featureflag.shared.model.FeatureFlagStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FeatureFlagCommandService {
    private final FeatureFlagRepository repository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public FeatureFlag register(RegisterFeatureFlagRequest request) {
        FeatureFlagEntity entity = new FeatureFlagEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());

        List<TargetingRuleEntity> rules = Optional.ofNullable(request.getTargetingRules())
                .orElseGet(List::of)
                .stream()
                .map(rule -> {
                    var ruleEntity = new TargetingRuleEntity();
                    ruleEntity.setName(rule.getName());
                    ruleEntity.setOperator(rule.getOperator());
                    ruleEntity.setValues(rule.getValues());
                    return ruleEntity;
                }).toList();

        entity.setTargetingRules(rules);

        FeatureFlagEntity saved = repository.save(entity);
        return saved.toDomainModel();
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
