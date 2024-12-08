package com.featureflag.shared.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class FeatureFlag {
    private Long id;
    private String name;
    private Status status;
    private Map<String, List<String>> criteria;
    private Instant createdAt;

    public enum Status {
        ON, OFF
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    
    public Map<String, List<String>> getCriteria() { return criteria; }
    public void setCriteria(Map<String, List<String>> criteria) { this.criteria = criteria; }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
