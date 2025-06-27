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
        if (values == null || values.isEmpty()) return false;
        var actualValue = criteria.get(name);

        if(RuleOperator.IN.equals(operator)) {
            return values.stream().anyMatch(expectedValue -> operator.matches(expectedValue, actualValue));
        }else if(RuleOperator.NOT_IN.equals(operator)) {
            return values.stream().allMatch(expectedValue -> operator.matches(expectedValue, actualValue));
        }

        return operator.matches(values.getFirst(), actualValue);
    }
}

