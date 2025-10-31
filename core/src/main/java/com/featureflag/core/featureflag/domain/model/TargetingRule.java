package com.featureflag.core.featureflag.domain.model;

import com.featureflag.shared.model.RuleOperator;

import java.util.List;
import java.util.Map;

public record TargetingRule(
        Long id,
        String name,
        RuleOperator operator,
        List<String> values
) {

    public TargetingRule {
        values = values == null ? List.of() : List.copyOf(values);
    }

    public boolean matches(Map<String, String> criteria) {
        if (criteria == null) {
            return false;
        }
        if (values.isEmpty()) {
            return false;
        }
        var actualValue = criteria.get(name);
        return operator.matches(values, actualValue);
    }
}
