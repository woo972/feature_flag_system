package com.featureflag.core.featureflag.presentation.dto;

import com.featureflag.shared.model.RuleOperator;

import java.util.List;

public record TargetingRuleResponse(
        Long id,
        String name,
        RuleOperator operator,
        List<String> values
) {
}
