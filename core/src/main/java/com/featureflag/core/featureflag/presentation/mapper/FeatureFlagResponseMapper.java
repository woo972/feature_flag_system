package com.featureflag.core.featureflag.presentation.mapper;

import com.featureflag.core.featureflag.domain.model.FeatureFlag;
import com.featureflag.core.featureflag.domain.model.TargetingRule;
import com.featureflag.core.featureflag.presentation.dto.FeatureFlagResponse;
import com.featureflag.core.featureflag.presentation.dto.TargetingRuleResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FeatureFlagResponseMapper {

    public FeatureFlagResponse toResponse(FeatureFlag featureFlag) {
        return new FeatureFlagResponse(
                featureFlag.id(),
                featureFlag.name(),
                featureFlag.description(),
                featureFlag.status(),
                featureFlag.targetingRules().stream()
                        .map(this::toResponse)
                        .toList(),
                featureFlag.createdAt(),
                featureFlag.updatedAt(),
                featureFlag.archivedAt()
        );
    }

    public List<FeatureFlagResponse> toResponse(List<FeatureFlag> featureFlags) {
        return featureFlags.stream()
                .map(this::toResponse)
                .toList();
    }

    public Page<FeatureFlagResponse> toResponse(Page<FeatureFlag> page) {
        return page.map(this::toResponse);
    }

    private TargetingRuleResponse toResponse(TargetingRule targetingRule) {
        return new TargetingRuleResponse(
                targetingRule.id(),
                targetingRule.name(),
                targetingRule.operator(),
                targetingRule.values()
        );
    }
}
