package com.featureflag.shared.model;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FeatureFlagTest {

    @Test
    void testFeatureFlagProperties() {
        // Arrange
        FeatureFlag flag = new FeatureFlag();
        Long id = 1L;
        String name = "test-flag";
        FeatureFlag.Status status = FeatureFlag.Status.ON;
        Map<String, List<String>> criteria = new HashMap<>();
        criteria.put("region", Arrays.asList("US", "EU"));
        Instant createdAt = Instant.now();

        // Act
        flag.setId(id);
        flag.setName(name);
        flag.setStatus(status);
        flag.setCriteria(criteria);
        flag.setCreatedAt(createdAt);

        // Assert
        assertEquals(id, flag.getId());
        assertEquals(name, flag.getName());
        assertEquals(status, flag.getStatus());
        assertEquals(criteria, flag.getCriteria());
        assertEquals(createdAt, flag.getCreatedAt());
    }

    @Test
    void testStatusEnum() {
        // Assert
        assertEquals(2, FeatureFlag.Status.values().length);
        assertTrue(Arrays.asList(FeatureFlag.Status.values()).contains(FeatureFlag.Status.ON));
        assertTrue(Arrays.asList(FeatureFlag.Status.values()).contains(FeatureFlag.Status.OFF));
    }
}
