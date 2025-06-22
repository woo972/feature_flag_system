package com.featureflag.core.entity;

import com.featureflag.core.repository.converter.MapToJsonConverter;
import com.featureflag.shared.model.FeatureFlag;
import com.featureflag.shared.model.FeatureFlagStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

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

    @Column(name = "criteria", nullable = true)
    @Convert(converter = MapToJsonConverter.class)
    private Map<String, Object> criteria;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @Column(name = "archved_at", nullable = true)
    private LocalDateTime archvedAt;

    public FeatureFlagEntity() {
        createdAt = LocalDateTime.now();
    }

    public FeatureFlag toDomainModel() {
        FeatureFlag featureFlag = new FeatureFlag();
        featureFlag.setId(id);
        featureFlag.setName(name);
        featureFlag.setDescription(description);
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
