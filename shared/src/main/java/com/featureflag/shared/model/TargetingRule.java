package com.featureflag.shared.model;

import java.util.Map;

@FunctionalInterface
public interface TargetingRule {
    boolean matches(Map<String, String> criteria);
}
