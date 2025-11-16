package com.featureflag.admin.dto;

import com.featureflag.shared.model.RuleOperator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@NoArgsConstructor
@AllArgsConstructor
public class CreatePreDefinedTargetingRuleRequest {
    @NotBlank
    @Size(max = 128)
    private String name;

    @Size(max = 512)
    private String description;

    @NotNull
    private RuleOperator operator;

    @NotNull
    @Size(min = 1, max = 20)
    private List<@NotBlank String> values;
}
