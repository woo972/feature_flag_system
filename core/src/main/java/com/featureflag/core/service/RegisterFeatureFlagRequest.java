package com.featureflag.core.service;

import com.featureflag.shared.model.TargetingRule;
import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterFeatureFlagRequest {
    private String name;
    private String description;
    private List<TargetingRule> targetingRules;
}
