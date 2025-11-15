package com.featureflag.core.predefinedtargetingrule.domain.model;

import com.featureflag.shared.model.RuleOperator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public record PreDefinedTargetingRule(
        Long id,
        String name,
        String description,
        RuleOperator operator,
        List<String> values,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public PreDefinedTargetingRule {
        values = values == null ? List.of() : List.copyOf(values);
    }

    public static PreDefinedTargetingRule create(
            String name,
            String description,
            RuleOperator operator,
            List<String> values) {
        var now = LocalDateTime.now();
        return new PreDefinedTargetingRule(null, name, description, operator, values, now, now);
    }

    public PreDefinedTargetingRule update(
            String description,
            RuleOperator operator,
            List<String> values) {
        return new PreDefinedTargetingRule(
                id, name, description, operator, values, createdAt, LocalDateTime.now());
    }

    public long requireId() {
        return Objects.requireNonNull(id, "PreDefinedTargetingRule id is required");
    }
}
