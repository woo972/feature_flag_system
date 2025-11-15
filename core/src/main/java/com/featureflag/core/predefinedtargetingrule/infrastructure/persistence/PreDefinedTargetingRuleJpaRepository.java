package com.featureflag.core.predefinedtargetingrule.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface PreDefinedTargetingRuleJpaRepository extends JpaRepository<PreDefinedTargetingRuleJpaEntity, Long> {
    Optional<PreDefinedTargetingRuleJpaEntity> findByName(String name);
}
