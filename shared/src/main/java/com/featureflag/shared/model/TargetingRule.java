package com.featureflag.shared.model;

import lombok.Builder;
import lombok.Getter;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class TargetingRule {
    private Long id;
    private String name;
    private RuleOperator operator;
    private List<String> values;

    public boolean matches(Map<String, String> criteria) {
        if (criteria == null) return false;
        if (values == null) return false;
        var actualValue = criteria.get(name);
        return values.stream().anyMatch(expectedValue -> operator.matches(expectedValue, actualValue));
    }
}

