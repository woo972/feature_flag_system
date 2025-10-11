package com.featureflag.core.repository;

import com.featureflag.core.entity.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.*;

import java.time.*;
import java.util.*;

@DataJpaTest
class FeatureFlagRepositoryTest {

    @Autowired
    private FeatureFlagRepository sut;

    @DisplayName("returns most recently updated flag")
    @Test
    public void findMostRecentUpdateFlag() {
        // Given
        var featureFlagEntity1 = createEntity(
                "flag1",
                LocalDateTime.of(2025, 10, 10, 12, 30)
        );

        var featureFlagEntity2 = createEntity(
                "flag2",
                LocalDateTime.of(2025, 10, 11, 12, 30)
        );

        sut.save(featureFlagEntity1);
        sut.save(featureFlagEntity2);

        // When
        var result = sut.findTopByOrderByUpdatedAtDesc();

        // Then
        Assertions.assertEquals("flag2", result.get().getName());
        Assertions.assertEquals(LocalDateTime.of(2025, 10, 11, 12, 30), result.get().getUpdatedAt());
    }

    @DisplayName("returns optional empty when there is no data")
    @Test
    public void emptyWhenNoData() {
        // When
        var result = sut.findTopByOrderByUpdatedAtDesc();

        // Then
        Assertions.assertEquals(Optional.empty(), result);
    }

    private FeatureFlagEntity createEntity(String name, LocalDateTime createdAt) {
        var flag = new FeatureFlagEntity();
        flag.setName(name);
        flag.setCreatedAt(createdAt);
        flag.setUpdatedAt(createdAt);
        return flag;
    }

}