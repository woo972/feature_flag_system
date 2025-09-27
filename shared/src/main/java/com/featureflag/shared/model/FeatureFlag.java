package com.featureflag.shared.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class FeatureFlag {
    private Long id;
    private String name;
    private String description;
    private FeatureFlagStatus status;
    private List<TargetingRule> targetingRules;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime archivedAt;

    @Builder()
    public FeatureFlag(Long id, String name, String description, FeatureFlagStatus status, List<TargetingRule> targetingRules, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime archivedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.targetingRules = targetingRules;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.archivedAt = archivedAt;
    }

    public String getKey() {
        return name;
    }

    public boolean evaluate(Map<String, String> criteria) {
        if (status.equals(FeatureFlagStatus.OFF)) return false;
        if (archivedAt != null) return false;
        if (targetingRules == null) return true;
        return targetingRules.stream().allMatch(rule -> rule.matches(criteria));
    }



    @Override
    public String toString() {
        return "FeatureFlag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", targetingRules=" + targetingRules +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", archivedAt=" + archivedAt +
                '}';
    }
}
