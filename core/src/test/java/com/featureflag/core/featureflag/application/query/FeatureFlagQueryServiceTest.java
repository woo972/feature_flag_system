package com.featureflag.core.featureflag.application.query;

import com.featureflag.core.featureflag.domain.model.FeatureFlag;
import com.featureflag.core.featureflag.domain.repository.FeatureFlagRepository;
import com.featureflag.shared.exception.FeatureFlagNotFoundException;
import com.featureflag.shared.model.FeatureFlagStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FeatureFlagQueryServiceTest {
    private FeatureFlagQueryService featureFlagQueryService;
    private FeatureFlagRepository featureFlagRepository;

    @BeforeEach
    void setup() {
        featureFlagRepository = mock(FeatureFlagRepository.class);
        featureFlagQueryService = new FeatureFlagQueryService(featureFlagRepository);
    }

    @DisplayName("list returns page of feature flags")
    @Test
    void list() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        var flag = existingFlag(1L, FeatureFlagStatus.ON, null, LocalDateTime.now());
        Page<FeatureFlag> page = new PageImpl<>(List.of(flag));
        when(featureFlagRepository.findAll(pageable)).thenReturn(page);

        var result = featureFlagQueryService.list(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(flag.id(), result.getContent().get(0).id());
    }

    @DisplayName("findAll returns all feature flags")
    @Test
    void findAll() {
        var flag = existingFlag(1L, FeatureFlagStatus.ON, null, LocalDateTime.now());
        when(featureFlagRepository.findAll()).thenReturn(List.of(flag));

        var result = featureFlagQueryService.findAll();

        assertEquals(1, result.size());
        assertEquals(flag.id(), result.getFirst().id());
    }

    @DisplayName("get returns flag by id")
    @Test
    void get() {
        var flag = existingFlag(1L, FeatureFlagStatus.ON, null, LocalDateTime.now());
        when(featureFlagRepository.findById(1L)).thenReturn(Optional.of(flag));

        var result = featureFlagQueryService.get(1L);

        assertEquals(flag.id(), result.id());
    }

    @DisplayName("get throws when flag missing")
    @Test
    void getThrowsWhenMissing() {
        when(featureFlagRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(FeatureFlagNotFoundException.class, () -> featureFlagQueryService.get(1L));
    }

    @DisplayName("evaluate uses domain logic")
    @Test
    void evaluate() {
        var flag = existingFlag(1L, FeatureFlagStatus.ON, null, LocalDateTime.now());
        when(featureFlagRepository.findById(1L)).thenReturn(Optional.of(flag));

        assertTrue(featureFlagQueryService.evaluate(1L, Map.of()));
    }

    @DisplayName("evaluate returns false when flag missing")
    @Test
    void evaluateMissing() {
        when(featureFlagRepository.findById(1L)).thenReturn(Optional.empty());

        assertFalse(featureFlagQueryService.evaluate(1L, Map.of()));
    }

    @DisplayName("getLastModifiedEpochTime returns updated timestamp")
    @Test
    void lastModified() {
        var updatedAt = LocalDateTime.of(2024, 1, 1, 0, 0);
        var flag = existingFlag(1L, FeatureFlagStatus.ON, null, updatedAt);
        when(featureFlagRepository.findLatest()).thenReturn(Optional.of(flag));

        var result = featureFlagQueryService.getLastModifiedEpochTime();

        assertEquals(updatedAt.toEpochSecond(ZoneOffset.UTC), result);
    }

    @DisplayName("getLastModifiedEpochTime returns zero when empty")
    @Test
    void lastModifiedEmpty() {
        when(featureFlagRepository.findLatest()).thenReturn(Optional.empty());

        assertEquals(0L, featureFlagQueryService.getLastModifiedEpochTime());
    }

    private FeatureFlag existingFlag(Long id, FeatureFlagStatus status, LocalDateTime archivedAt, LocalDateTime updatedAt) {
        var createdAt = updatedAt.minusDays(1);
        return new FeatureFlag(id, "flag", "description", status, List.of(), createdAt, updatedAt, archivedAt);
    }
}
