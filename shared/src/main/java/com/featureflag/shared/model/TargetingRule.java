package com.featureflag.shared.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TargetingRule {
    private Long id;
    private String name;
    private RuleOperator operator;
    private List<String> values;

    public boolean matches(Map<String, String> criteria) {
        if (criteria == null) return false;
        if (values == null || values.isEmpty()) return false;
        var actualValue = criteria.get(name);

        return operator.matches(values, actualValue);
    }
}

