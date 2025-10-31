package com.featureflag.core.featureflag.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface FeatureFlagJpaRepository extends JpaRepository<FeatureFlagJpaEntity, Long> {
    Optional<FeatureFlagJpaEntity> findTopByOrderByUpdatedAtDesc();
}
