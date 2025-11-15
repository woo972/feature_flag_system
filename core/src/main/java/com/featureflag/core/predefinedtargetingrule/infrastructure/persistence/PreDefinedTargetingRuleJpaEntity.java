package com.featureflag.core.predefinedtargetingrule.infrastructure.persistence;

import com.featureflag.core.predefinedtargetingrule.domain.model.PreDefinedTargetingRule;
import com.featureflag.shared.model.RuleOperator;
import jakarta.persistence.Column;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "pre_defined_targeting_rule")
@Entity
class PreDefinedTargetingRuleJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RuleOperator operator;

    @Column(name = "rule_values", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> values;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    PreDefinedTargetingRule toDomain() {
        return new PreDefinedTargetingRule(id, name, description, operator, values, createdAt, updatedAt);
    }

    void apply(PreDefinedTargetingRule rule) {
        id = rule.id();
        name = rule.name();
        description = rule.description();
        operator = rule.operator();
        values = rule.values();
        createdAt = rule.createdAt();
        updatedAt = rule.updatedAt();
    }

    static PreDefinedTargetingRuleJpaEntity fromDomain(PreDefinedTargetingRule rule) {
        var entity = new PreDefinedTargetingRuleJpaEntity();
        entity.apply(rule);
        return entity;
    }
}
