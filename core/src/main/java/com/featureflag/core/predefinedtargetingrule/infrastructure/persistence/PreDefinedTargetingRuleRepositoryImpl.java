package com.featureflag.core.predefinedtargetingrule.infrastructure.persistence;

import com.featureflag.core.predefinedtargetingrule.domain.model.PreDefinedTargetingRule;
import com.featureflag.core.predefinedtargetingrule.domain.repository.PreDefinedTargetingRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
class PreDefinedTargetingRuleRepositoryImpl implements PreDefinedTargetingRuleRepository {
    private final PreDefinedTargetingRuleJpaRepository jpaRepository;

    @Override
    @Transactional
    public PreDefinedTargetingRule save(PreDefinedTargetingRule rule) {
        var entity = rule.id() == null
                ? PreDefinedTargetingRuleJpaEntity.fromDomain(rule)
                : jpaRepository.findById(rule.id())
                .map(existing -> {
                    existing.apply(rule);
                    return existing;
                })
                .orElseGet(() -> PreDefinedTargetingRuleJpaEntity.fromDomain(rule));
        var saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<PreDefinedTargetingRule> findById(long id) {
        return jpaRepository.findById(id).map(PreDefinedTargetingRuleJpaEntity::toDomain);
    }

    @Override
    public Optional<PreDefinedTargetingRule> findByName(String name) {
        return jpaRepository.findByName(name).map(PreDefinedTargetingRuleJpaEntity::toDomain);
    }

    @Override
    public List<PreDefinedTargetingRule> findAll() {
        return jpaRepository.findAll().stream()
                .map(PreDefinedTargetingRuleJpaEntity::toDomain)
                .toList();
    }

    @Override
    public Page<PreDefinedTargetingRule> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable)
                .map(PreDefinedTargetingRuleJpaEntity::toDomain);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        jpaRepository.deleteById(id);
    }
}
