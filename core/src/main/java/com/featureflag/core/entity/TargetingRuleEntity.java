package com.featureflag.core.entity;

import com.featureflag.shared.model.RuleOperator;
import com.featureflag.shared.model.TargetingRule;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

/*
rule 예시
name: age
operator: gt
value: 20

name: region
operator: in
value: ["seoul", "busan"]
* */


@Getter
@Setter
@NoArgsConstructor
@Table(name = "targeting_rule")
@Entity
public class TargetingRuleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "feature_flag_id", nullable = false)
    private Long featureFlagId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RuleOperator operator;
    @Column(name = "rule_value", nullable = false)
    private String value;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = true)
    private LocalDateTime updatedAt;

    public TargetingRule toDomainModel() {
        return TargetingRule.builder()
                .id(id)
                .name(name)
                .operator(operator)
                .values(List.of(value))
                .build();
    }
}
