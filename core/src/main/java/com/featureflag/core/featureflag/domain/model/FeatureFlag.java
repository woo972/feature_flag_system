package com.featureflag.core.featureflag.domain.model;

import com.featureflag.shared.exception.FeatureFlagNotUpdatedException;
import com.featureflag.shared.model.FeatureFlagStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record FeatureFlag(
        Long id,
        String name,
        String description,
        FeatureFlagStatus status,
        List<TargetingRule> targetingRules,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime archivedAt
) {

    public FeatureFlag {
        targetingRules = targetingRules == null ? List.of() : List.copyOf(targetingRules);
    }

    public static FeatureFlag create(String name, String description, List<TargetingRule> targetingRules) {
        var now = LocalDateTime.now();
        return new FeatureFlag(null, name, description, FeatureFlagStatus.OFF, targetingRules, now, now, null);
    }

    public FeatureFlag turnOn() {
        if (status == FeatureFlagStatus.ON || archivedAt != null) {
            throw new FeatureFlagNotUpdatedException(requireId());
        }
        return withStatus(FeatureFlagStatus.ON);
    }

    public FeatureFlag turnOff() {
        if (status == FeatureFlagStatus.OFF) {
            throw new FeatureFlagNotUpdatedException(requireId());
        }
        return withStatus(FeatureFlagStatus.OFF);
    }

    public FeatureFlag archive() {
        if (archivedAt != null) {
            throw new FeatureFlagNotUpdatedException(requireId());
        }
        return new FeatureFlag(id, name, description, status, targetingRules, createdAt, LocalDateTime.now(), LocalDateTime.now());
    }

    public boolean evaluate(Map<String, String> criteria) {
        if (status == FeatureFlagStatus.OFF) {
            return false;
        }
        if (archivedAt != null) {
            return false;
        }
        if (targetingRules.isEmpty()) {
            return true;
        }
        return targetingRules.stream().allMatch(rule -> rule.matches(criteria));
    }

    private FeatureFlag withStatus(FeatureFlagStatus newStatus) {
        return new FeatureFlag(id, name, description, newStatus, targetingRules, createdAt, LocalDateTime.now(), archivedAt);
    }

    private long requireId() {
        return Objects.requireNonNull(id, "Feature flag id is required");
    }
}
