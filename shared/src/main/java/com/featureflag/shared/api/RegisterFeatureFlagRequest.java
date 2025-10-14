package com.featureflag.shared.api;

import com.featureflag.shared.model.TargetingRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
