package com.featureflag.core.predefinedtargetingrule.presentation.dto;

import com.featureflag.shared.model.RuleOperator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePreDefinedTargetingRuleRequest {
    @Size(max = 512)
    private String description;

    @NotNull
    private RuleOperator operator;

    @NotNull
    @Size(min = 1, max = 20)
    private List<@NotBlank String> values;
}
