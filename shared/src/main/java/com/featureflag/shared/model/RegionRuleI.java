package com.featureflag.shared.model;

import java.util.Map;

public class RegionRuleI implements ITargetingRule {
    private String region;

    @Override
    public boolean matches(Map<String, String> criteria) {
        if (criteria == null) return false;
        if (criteria.get("region") == null) return false;
        return criteria.get("region").contains(region);
    }
}
