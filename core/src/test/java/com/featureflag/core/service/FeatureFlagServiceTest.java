package com.featureflag.core.service;

import com.featureflag.core.entity.FeatureFlagEntity;
import com.featureflag.core.repository.FeatureFlagRepository;
import com.featureflag.shared.model.FeatureFlag;
import com.featureflag.shared.model.FeatureFlagStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

class FeatureFlagServiceTest {

    FeatureFlagService sut;
    FeatureFlagRepository flagRepository;

    @BeforeEach
    void setup() {
        flagRepository = mock(FeatureFlagRepository.class);
        sut = new FeatureFlagService(flagRepository);
    }

    @DisplayName("get all feature flags")
    @Test
    public void getAllFeatureFlags() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));

        FeatureFlagEntity entity = new FeatureFlagEntity(
                1L,
                "flag",
                "description",
                FeatureFlagStatus.ON,
                null,
                null,
                null,
                null
        );
        Page<FeatureFlagEntity> page = new PageImpl<>(List.of(entity));
        when(flagRepository.findAll(pageable)).thenReturn(page);

        Page<FeatureFlag> result = sut.list(pageable);

        FeatureFlag flag = result.getContent().get(0);
        Assertions.assertEquals(1, result.getContent().size());
        Assertions.assertEquals(entity.getId(), flag.getId());
        Assertions.assertEquals(entity.getName(), flag.getName());
        Assertions.assertEquals(entity.getDescription(), flag.getDescription());
        Assertions.assertEquals(entity.getStatus(), flag.getStatus());
        Assertions.assertEquals(entity.getTargetingRules(), flag.getTargetingRules());
        Assertions.assertEquals(entity.getCreatedAt(), flag.getCreatedAt());
        Assertions.assertEquals(entity.getUpdatedAt(), flag.getUpdatedAt());
        Assertions.assertEquals(entity.getArchivedAt(), flag.getArchivedAt());
        Assertions.assertEquals(0, result.getNumber());
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(1, result.getTotalPages());
    }

    @DisplayName("get feature flag by id")
    @Test
    public void getFeatureFlagById() {
        Long id = 1L;
        FeatureFlagEntity entity = new FeatureFlagEntity(
                id,
                "flag",
                "description",
                FeatureFlagStatus.ON,
                null,
                null,
                null,
                null
        );

        when(flagRepository.findById(id)).thenReturn(Optional.of(entity));

        FeatureFlag result = sut.get(id);

        Assertions.assertEquals(entity.getId(), result.getId());
        Assertions.assertEquals(entity.getName(), result.getName());
        Assertions.assertEquals(entity.getDescription(), result.getDescription());
        Assertions.assertEquals(entity.getStatus(), result.getStatus());
        Assertions.assertEquals(entity.getTargetingRules(), result.getTargetingRules());
        Assertions.assertEquals(entity.getCreatedAt(), result.getCreatedAt());
        Assertions.assertEquals(entity.getUpdatedAt(), result.getUpdatedAt());
        Assertions.assertEquals(entity.getArchivedAt(), result.getArchivedAt());
    }


    @DisplayName("invoke repository save method when register feature flag")
    @Test
    public void invokeRepositorySaveMethod() {
        RegisterFeatureFlagRequest request = new RegisterFeatureFlagRequest(
                "feature-1",
                "desc",
                new HashMap<>() {{
                    put("key", "value");
                }});

        sut.register(request);

        verify(flagRepository).save(any(FeatureFlagEntity.class));
    }


    @DisplayName("returns true when flag is on and criteria matches")
    @Test
    public void returnsTrueWhenFlagIsOnAndCriteriaMatches() {
        Long flagId = 1L;
        Map<String, String> criteria = null;
        FeatureFlagEntity flag = new FeatureFlagEntity(
                flagId,
                "flag",
                "description",
                FeatureFlagStatus.ON,
                null,
                null,
                null,
                null
        );
        when(flagRepository.findById(flagId)).thenReturn(Optional.of(flag));
        boolean result = sut.evaluate(flagId, criteria);
        Assertions.assertTrue(result);
    }

    @DisplayName("returns false when flag is not exists")
    @Test
    public void returnsFalseWhenFlagIsNotExists() {
        Long flagId = 1L;
        Map<String, String> criteria = null;
        when(flagRepository.findById(flagId)).thenReturn(Optional.empty());
        boolean result = sut.evaluate(flagId, criteria);
        Assertions.assertFalse(result);
    }

    @DisplayName("returns false when flag is off")
    @Test
    public void returnsFalseWhenFlagIsOff() {
        Long flagId = 1L;
        Map<String, String> criteria = null;
        FeatureFlagEntity flag = new FeatureFlagEntity(
                flagId,
                "flag",
                "description",
                FeatureFlagStatus.OFF,
                null,
                null,
                null,
                null);
        when(flagRepository.findById(flagId)).thenReturn(Optional.empty());
        boolean result = sut.evaluate(flagId, criteria);
        Assertions.assertFalse(result);
    }
}