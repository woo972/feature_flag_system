package com.featureflag.core.featureflag.infrastructure.persistence;

import com.featureflag.core.featureflag.domain.model.FeatureFlag;
import com.featureflag.core.featureflag.domain.repository.FeatureFlagRepository;
import com.featureflag.shared.model.FeatureFlagStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(FeatureFlagRepositoryImpl.class)
class FeatureFlagRepositoryTest {
    @Autowired
    private FeatureFlagRepository featureFlagRepository;

    @DisplayName("returns most recently updated flag")
    @Test
    void findMostRecentUpdateFlag() {
        var earlier = createFlag("flag1", LocalDateTime.of(2025, 10, 10, 12, 30));
        var later = createFlag("flag2", LocalDateTime.of(2025, 10, 11, 12, 30));
        featureFlagRepository.save(earlier);
        featureFlagRepository.save(later);

        var result = featureFlagRepository.findLatest();

        assertTrue(result.isPresent());
        assertEquals("flag2", result.orElseThrow().name());
        assertEquals(LocalDateTime.of(2025, 10, 11, 12, 30), result.orElseThrow().updatedAt());
    }

    @DisplayName("returns optional empty when there is no data")
    @Test
    void emptyWhenNoData() {
        var result = featureFlagRepository.findLatest();

        assertTrue(result.isEmpty());
    }

    private FeatureFlag createFlag(String name, LocalDateTime timestamps) {
        return new FeatureFlag(
                null,
                name,
                "description",
                FeatureFlagStatus.ON,
                List.of(),
                timestamps,
                timestamps,
                null
        );
    }
}
