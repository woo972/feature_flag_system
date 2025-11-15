package com.featureflag.core.featureflag.application.command;

import com.featureflag.core.featureflag.domain.event.FeatureFlagUpdatedEvent;
import com.featureflag.core.featureflag.domain.model.FeatureFlag;
import com.featureflag.core.featureflag.domain.model.TargetingRule;
import com.featureflag.core.featureflag.domain.repository.FeatureFlagRepository;
import com.featureflag.core.predefinedtargetingrule.application.PreDefinedTargetingRuleService;
import com.featureflag.shared.api.RegisterFeatureFlagRequest;
import com.featureflag.shared.api.targeting.TargetingRuleRequest;
import com.featureflag.shared.exception.FeatureFlagNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FeatureFlagCommandService {
    private final FeatureFlagRepository featureFlagRepository;
    private final PreDefinedTargetingRuleService preDefinedTargetingRuleService;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public FeatureFlag register(RegisterFeatureFlagRequest request) {
        var targetingRules = new ArrayList<TargetingRule>();

        Optional.ofNullable(request.getTargetingRules())
                .orElseGet(List::of)
                .stream()
                .map(this::toDomainRule)
                .forEach(targetingRules::add);

        Optional.ofNullable(request.getPreDefinedTargetingRuleIds())
                .orElseGet(List::of)
                .stream()
                .map(preDefinedTargetingRuleService::findById)
                .map(preDefinedRule -> new TargetingRule(
                        null,
                        preDefinedRule.name(),
                        preDefinedRule.operator(),
                        preDefinedRule.values()
                ))
                .forEach(targetingRules::add);

        var featureFlag = FeatureFlag.create(request.getName(), request.getDescription(), targetingRules);
        return featureFlagRepository.save(featureFlag);
    }

    @CacheEvict(value = "featureFlags", allEntries = true)
    public void refreshCache() {
    }

    @CacheEvict(value = "featureFlags", key = "#id")
    @Transactional
    public FeatureFlag on(long id) {
        var featureFlag = featureFlagRepository.findById(id)
                .orElseThrow(() -> new FeatureFlagNotFoundException(id));
        var updated = featureFlag.turnOn();
        var saved = featureFlagRepository.save(updated);
        publisher.publishEvent(new FeatureFlagUpdatedEvent(saved));
        return saved;
    }

    @CacheEvict(value = "featureFlags", key = "#id")
    @Transactional
    public FeatureFlag off(long id) {
        var featureFlag = featureFlagRepository.findById(id)
                .orElseThrow(() -> new FeatureFlagNotFoundException(id));
        var updated = featureFlag.turnOff();
        var saved = featureFlagRepository.save(updated);
        publisher.publishEvent(new FeatureFlagUpdatedEvent(saved));
        return saved;
    }

    @CacheEvict(value = "featureFlags", key = "#id")
    @Transactional
    public FeatureFlag archive(long id) {
        var featureFlag = featureFlagRepository.findById(id)
                .orElseThrow(() -> new FeatureFlagNotFoundException(id));
        var updated = featureFlag.archive();
        var saved = featureFlagRepository.save(updated);
        publisher.publishEvent(new FeatureFlagUpdatedEvent(saved));
        return saved;
    }

    private TargetingRule toDomainRule(TargetingRuleRequest request) {
        return new TargetingRule(null, request.getName(), request.getOperator(), request.getValues());
    }
}
