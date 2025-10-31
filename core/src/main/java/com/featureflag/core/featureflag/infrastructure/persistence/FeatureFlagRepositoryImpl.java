package com.featureflag.core.featureflag.infrastructure.persistence;

import com.featureflag.core.featureflag.domain.model.FeatureFlag;
import com.featureflag.core.featureflag.domain.repository.FeatureFlagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
class FeatureFlagRepositoryImpl implements FeatureFlagRepository {
    private final FeatureFlagJpaRepository featureFlagJpaRepository;

    @Override
    @Transactional
    public FeatureFlag save(FeatureFlag featureFlag) {
        var entity = featureFlag.id() == null
                ? FeatureFlagJpaEntity.fromDomain(featureFlag)
                : featureFlagJpaRepository.findById(featureFlag.id())
                .map(existing -> {
                    existing.apply(featureFlag);
                    return existing;
                })
                .orElseGet(() -> FeatureFlagJpaEntity.fromDomain(featureFlag));
        var saved = featureFlagJpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<FeatureFlag> findById(long id) {
        return featureFlagJpaRepository.findById(id).map(FeatureFlagJpaEntity::toDomain);
    }

    @Override
    public List<FeatureFlag> findAll() {
        return featureFlagJpaRepository.findAll().stream()
                .map(FeatureFlagJpaEntity::toDomain)
                .toList();
    }

    @Override
    public Page<FeatureFlag> findAll(Pageable pageable) {
        return featureFlagJpaRepository.findAll(pageable)
                .map(FeatureFlagJpaEntity::toDomain);
    }

    @Override
    public Optional<FeatureFlag> findLatest() {
        return featureFlagJpaRepository.findTopByOrderByUpdatedAtDesc()
                .map(FeatureFlagJpaEntity::toDomain);
    }
}
