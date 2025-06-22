package com.featureflag.shared.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

@Getter
@Setter
public class FeatureFlag {
    private Long id;
    private String name;
    private String description;
    private FeatureFlagStatus status = FeatureFlagStatus.OFF;
    private List<TargetingRule> rules = emptyList();
    private Map<String, Object> criteria = emptyMap();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime archivedAt;

    public boolean evaluate(Map<String, String> criteria) {
        return rules.stream().allMatch(rule -> rule.matches(criteria));
    }
}
