package com.featureflag.core.featureflag.infrastructure.persistence;

import com.featureflag.core.featureflag.domain.model.FeatureFlag;
import com.featureflag.core.featureflag.domain.model.TargetingRule;
import com.featureflag.shared.model.FeatureFlagStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "feature_flags")
@Entity
class FeatureFlagJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "status", nullable = false)
    private FeatureFlagStatus status = FeatureFlagStatus.OFF;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "feature_flag_id")
    private List<TargetingRuleJpaEntity> targetingRules = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    FeatureFlag toDomain() {
        var rules = targetingRules.stream()
                .map(TargetingRuleJpaEntity::toDomain)
                .toList();
        return new FeatureFlag(id, name, description, status, rules, createdAt, updatedAt, archivedAt);
    }

    void apply(FeatureFlag featureFlag) {
        id = featureFlag.id();
        name = featureFlag.name();
        description = featureFlag.description();
        status = featureFlag.status();
        createdAt = featureFlag.createdAt();
        updatedAt = featureFlag.updatedAt();
        archivedAt = featureFlag.archivedAt();
        targetingRules.clear();
        targetingRules.addAll(featureFlag.targetingRules().stream()
                .map(TargetingRuleJpaEntity::fromDomain)
                .toList());
    }

    static FeatureFlagJpaEntity fromDomain(FeatureFlag featureFlag) {
        var entity = new FeatureFlagJpaEntity();
        entity.apply(featureFlag);
        return entity;
    }
}
