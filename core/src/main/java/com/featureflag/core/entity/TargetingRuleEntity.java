package com.featureflag.core.entity;

import com.featureflag.shared.model.RuleOperator;
import com.featureflag.shared.model.TargetingRule;
import jakarta.persistence.*;
import jakarta.persistence.EnumType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.hibernate.type.*;

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
@Table(name = "targeting_rule")
@Entity
public class TargetingRuleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RuleOperator operator;
    @Column(name = "rule_values", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> values;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = true)
    private LocalDateTime updatedAt;

    public TargetingRuleEntity(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public TargetingRule toDomainModel() {
        return TargetingRule.builder()
                .id(id)
                .name(name)
                .operator(operator)
                .values(values)
                .build();
    }
}
