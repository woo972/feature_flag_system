package com.featureflag.core.predefinedtargetingrule.presentation.dto;

import com.featureflag.shared.model.RuleOperator;

import java.time.LocalDateTime;
import java.util.List;

public record PreDefinedTargetingRuleResponse(
        Long id,
        String name,
        String description,
        RuleOperator operator,
        List<String> values,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
