package com.featureflag.shared.api;

import com.featureflag.shared.api.targeting.TargetingRuleRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
    @NotBlank
    @Size(max = 128)
    private String name;
    @Size(max = 512)
    private String description;
    @Valid
    private List<TargetingRuleRequest> targetingRules;
    @Size(max = 50)
    private List<@Positive Long> preDefinedTargetingRuleIds;
}
