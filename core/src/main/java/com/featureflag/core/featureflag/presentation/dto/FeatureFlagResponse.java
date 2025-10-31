package com.featureflag.core.featureflag.presentation.dto;

import com.featureflag.shared.model.FeatureFlagStatus;

import java.time.LocalDateTime;
import java.util.List;

public record FeatureFlagResponse(
        Long id,
        String name,
        String description,
        FeatureFlagStatus status,
        List<TargetingRuleResponse> targetingRules,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime archivedAt
) {
}
