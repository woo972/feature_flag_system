package com.featureflag.core.featureflag.infrastructure.persistence;

import com.featureflag.core.featureflag.domain.model.TargetingRule;
import com.featureflag.core.repository.converter.ListToJsonConverter;
import com.featureflag.shared.model.RuleOperator;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "targeting_rule")
@Entity
class TargetingRuleJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RuleOperator operator;

    @Column(name = "rule_values", nullable = false)
    @Convert(converter = ListToJsonConverter.class)
    private List<String> values;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime updatedAt = LocalDateTime.now();

    TargetingRule toDomain() {
        return new TargetingRule(id, name, operator, values);
    }

    static TargetingRuleJpaEntity fromDomain(TargetingRule targetingRule) {
        var entity = new TargetingRuleJpaEntity();
        entity.id = targetingRule.id();
        entity.name = targetingRule.name();
        entity.operator = targetingRule.operator();
        entity.values = targetingRule.values();
        return entity;
    }
}
