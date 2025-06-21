package com.featureflag.shared.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

public class FeatureFlag {
    private Long id;
    private String name;
    private FeatureFlagStatus status = FeatureFlagStatus.OFF;
    private List<TargetingRule> rules = emptyList();
    private Map<String, Object> criteria = emptyMap();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime archivedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public FeatureFlagStatus getStatus() { return status; }
    public void setStatus(FeatureFlagStatus status) { this.status = status; }
    
    public Map<String, Object> getCriteria() { return criteria; }
    public void setCriteria(Map<String, Object> criteria) { this.criteria = criteria; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getArchivedAt() { return archivedAt; }
    public void setArchivedAt(LocalDateTime archivedAt) { this.archivedAt = archivedAt; }

    public boolean evaluate(Map<String, String> criteria) {
        return rules.stream().allMatch(rule -> rule.matches(criteria));
    }
}
