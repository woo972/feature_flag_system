package com.featureflag.shared.model;

import java.util.Map;

public class RegionRule implements TargetingRule {
    private String region;

    @Override
    public boolean matches(Map<String, String> criteria) {
        if (criteria == null || criteria.isEmpty()) return true;
        if (criteria.get("region") == null) return true;
        return criteria.get("region").contains(region);
    }
}
