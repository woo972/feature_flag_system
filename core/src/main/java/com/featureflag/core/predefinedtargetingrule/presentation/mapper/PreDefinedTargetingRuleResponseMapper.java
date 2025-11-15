package com.featureflag.core.predefinedtargetingrule.presentation.mapper;

import com.featureflag.core.predefinedtargetingrule.domain.model.PreDefinedTargetingRule;
import com.featureflag.core.predefinedtargetingrule.presentation.dto.PreDefinedTargetingRuleResponse;
import org.springframework.stereotype.Component;

@Component
public class PreDefinedTargetingRuleResponseMapper {
    public PreDefinedTargetingRuleResponse toResponse(PreDefinedTargetingRule rule) {
        return new PreDefinedTargetingRuleResponse(
                rule.id(),
                rule.name(),
                rule.description(),
                rule.operator(),
                rule.values(),
                rule.createdAt(),
                rule.updatedAt()
        );
    }
}
