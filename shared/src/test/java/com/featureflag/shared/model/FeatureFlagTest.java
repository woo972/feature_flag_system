package com.featureflag.shared.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class FeatureFlagTest {

    @DisplayName("returns false when status is OFF")
    @Test
    void evaluatePrecondition() {
        var featureFlag = FeatureFlag.builder()
                .status(FeatureFlagStatus.OFF)
                .build();

        assertFalse(featureFlag.evaluate(Map.of()));
    }

    @DisplayName("returns false when archivedAt is not null")
    @Test
    void evaluatePrecondition2() {
        var featureFlag = FeatureFlag.builder()
                .status(FeatureFlagStatus.ON)
                .archivedAt(LocalDateTime.now())
                .build();

        assertFalse(featureFlag.evaluate(Map.of()));
    }

    @DisplayName("returns true when targetingRules is null")
    @Test
    void evaluatePrecondition3() {
        var featureFlag = FeatureFlag.builder()
                .status(FeatureFlagStatus.ON)
                .build();

        assertTrue(featureFlag.evaluate(Map.of()));
    }

    @DisplayName("returns true when targetingRules are matches")
    @Test
    void returnsTrueWhenTargetingRulesAreMatches() {
        var featureFlag = FeatureFlag.builder()
                .status(FeatureFlagStatus.ON)
                .targetingRules(List.of(TargetingRule.builder()
                        .name("region")
                        .operator(RuleOperator.EQUAL)
                        .values(List.of("KR"))
                        .build()))
                .build();

        assertTrue(featureFlag.evaluate(Map.of("region", "KR")));
    }

    @DisplayName("returns id when present")
    @Test
    void getKeyReturnsId() {
        var featureFlag = FeatureFlag.builder()
                .id(42L)
                .status(FeatureFlagStatus.ON)
                .build();

        assertEquals(42L, featureFlag.getId());
    }

    @DisplayName("throws when id is missing")
    @Test
    void getIdThrowsWhenIdMissing() {
        var featureFlag = FeatureFlag.builder()
                .status(FeatureFlagStatus.ON)
                .build();

        assertThrows(IllegalStateException.class, featureFlag::getId);
    }
}
