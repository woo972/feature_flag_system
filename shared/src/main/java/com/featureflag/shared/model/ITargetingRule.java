package com.featureflag.shared.model;

import java.util.Map;

@FunctionalInterface
public interface ITargetingRule {
    boolean matches(Map<String, String> criteria);
}
