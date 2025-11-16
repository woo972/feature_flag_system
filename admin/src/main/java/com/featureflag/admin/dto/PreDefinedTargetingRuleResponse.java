package com.featureflag.admin.dto;

import com.featureflag.shared.model.RuleOperator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreDefinedTargetingRuleResponse {
    private Long id;
    private String name;
    private String description;
    private RuleOperator operator;
    private List<String> values;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
