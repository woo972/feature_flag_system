package com.featureflag.shared.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class FeatureFlag {
    private Long id;
    private String name;
    private String description;
    private FeatureFlagStatus status;
    private List<TargetingRule> targetingRules;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime archivedAt;

    public boolean evaluate(Map<String, String> criteria) {
        if (status.equals(FeatureFlagStatus.OFF)) return false;
        if (archivedAt != null) return false;
        if (targetingRules == null) return true;
        return targetingRules.stream().allMatch(rule -> rule.matches(criteria));
    }
}
