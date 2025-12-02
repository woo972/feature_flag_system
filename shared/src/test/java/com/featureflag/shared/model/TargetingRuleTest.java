package com.featureflag.shared.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TargetingRuleTest {

    @DisplayName("returns true if the rule values contain the criteria and the operator is IN")
    @Test
    public void returnsTrueIfRuleMatches() {
        String ruleName = "region";
        Map<String, String> criteria = new HashMap<>(){{
            put(ruleName, "KR");
        }};

        var rule = TargetingRule.builder()
                .id(1L)
                .name(ruleName)
                .operator(RuleOperator.IN)
                .values(List.of("US", "KR"))
                .build();

        assertTrue(rule.matches(criteria));
    }

    @DisplayName("returns true if the rule values doesn't contain the criteria and the operator is NOT_IN")
    @Test
    public void returnsTrueIfRuleMatches2() {
        String ruleName = "region";
        Map<String, String> criteria = new HashMap<>(){{
            put(ruleName, "KR");
        }};
        var rule = TargetingRule.builder()
                .id(1L)
                .name(ruleName)
                .operator(RuleOperator.NOT_IN)
                .values(List.of("US", "JP"))
                .build();

        assertTrue(rule.matches(criteria));
    }

    @DisplayName("returns true if the rule value equals the criteria and the operator is EQUAL")
    @Test
    public void returnsTrueIfRuleMatches3() {
        String ruleName = "region";
        Map<String, String> criteria = new HashMap<>(){{
            put(ruleName, "KR");
        }};
        var rule = TargetingRule.builder()
                .id(1L)
                .name(ruleName)
                .operator(RuleOperator.EQUAL)
                .values(List.of("KR"))
                .build();

        assertTrue(rule.matches(criteria));
    }

    @DisplayName("returns false if the rule values are empty")
    @Test
    public void returnsFalseIfRuleMatches4() {
        String ruleName = "region";
        Map<String, String> criteria = new HashMap<>() {{
            put(ruleName, "KR");
        }};
        var rule = TargetingRule.builder()
                .id(1L)
                .name(ruleName)
                .operator(RuleOperator.EQUAL)
                .values(List.of())
                .build();

        assertFalse(rule.matches(criteria));
    }

    @DisplayName("returns false if the rule criteria are null")
    @Test
    public void returnsFalseIfRuleMatches5() {
        String ruleName = "region";
        Map<String, String> criteria = null;
        var rule = TargetingRule.builder()
                .id(1L)
                .name(ruleName)
                .operator(RuleOperator.EQUAL)
                .values(List.of())
                .build();

        assertFalse(rule.matches(criteria));
    }

    @DisplayName("returns false if the rule name is null")
    @Test
    public void returnsFalseIfRuleMatches6() {
        String ruleName = null;
        Map<String, String> criteria = new HashMap<>() {{
            put(ruleName, null);
        }};
        var rule = TargetingRule.builder()
                .id(1L)
                .name(ruleName)
                .operator(RuleOperator.EQUAL)
                .values(List.of("US"))
                .build();

        assertFalse(rule.matches(criteria));
    }
}