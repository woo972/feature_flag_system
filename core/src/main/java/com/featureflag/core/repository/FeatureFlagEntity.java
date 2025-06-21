package com.featureflag.core.repository;

import com.featureflag.shared.model.FeatureFlag;
import com.featureflag.shared.model.FeatureFlagStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Table(name = "feature_flags")
public class FeatureFlagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private FeatureFlagStatus status = FeatureFlagStatus.OFF;

    @Column(name = "criteria", nullable = true)
    private Map<String, Object> criteria;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @Column(name = "archved_at", nullable = true)
    private LocalDateTime archvedAt;

    public FeatureFlag toDomainModel() {
        FeatureFlag featureFlag = new FeatureFlag();
        featureFlag.setId(id);
        featureFlag.setName(name);
        featureFlag.setStatus(status);
        if(criteria != null) {
            featureFlag.setCriteria(criteria);
        }
        featureFlag.setCreatedAt(createdAt);
        featureFlag.setUpdatedAt(updatedAt);
        featureFlag.setArchivedAt(archvedAt);
        return featureFlag;
    }
}
