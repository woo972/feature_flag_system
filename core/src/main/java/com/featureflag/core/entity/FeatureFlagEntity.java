package com.featureflag.core.entity;

import com.featureflag.shared.model.FeatureFlag;
import com.featureflag.shared.model.FeatureFlagStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Getter
@Setter
@Table(name = "feature_flags")
@Entity
public class FeatureFlagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private FeatureFlagStatus status = FeatureFlagStatus.OFF;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "feature_flag_id")
    private List<TargetingRuleEntity> targetingRules;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @Column(name = "archived_at", nullable = true)
    private LocalDateTime archivedAt;

    public FeatureFlagEntity() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    public FeatureFlag toDomainModel() {
        return FeatureFlag.builder()
                .id(id)
                .name(name)
                .description(description)
                .status(status)
                .targetingRules(
                        Optional.ofNullable(targetingRules)
                                .map(rules -> rules.stream()
                                        .map(TargetingRuleEntity::toDomainModel)
                                        .toList())
                                .orElse(null))
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .archivedAt(archivedAt)
                .build();
    }
}
