package com.featureflag.shared.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class FeatureFlag {
    private Long id;
    private String name;
    private String description;
    private FeatureFlagStatus status = FeatureFlagStatus.OFF;
    private List<TargetingRule> rules;
    private Map<String, Object> criteria;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime archivedAt;

    public boolean evaluate(Map<String, String> criteria) {
        if (status.equals(FeatureFlagStatus.OFF)) return false;
        if (archivedAt != null) return false;
        if (rules == null) return true;
        return rules.stream().allMatch(rule -> rule.matches(criteria));
    }
}
