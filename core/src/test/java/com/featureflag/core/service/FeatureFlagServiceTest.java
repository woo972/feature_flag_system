package com.featureflag.core.service;

import com.featureflag.core.entity.FeatureFlagEntity;
import com.featureflag.core.repository.FeatureFlagRepository;
import com.featureflag.shared.exception.FeatureFlagNotUpdatedException;
import com.featureflag.shared.model.FeatureFlag;
import com.featureflag.shared.model.FeatureFlagStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.mockito.Mockito.*;

class FeatureFlagServiceTest {

    FeatureFlagService sut;
    FeatureFlagRepository flagRepository;
    ApplicationEventPublisher publisher;

    @BeforeEach
    void setup() {
        flagRepository = mock(FeatureFlagRepository.class);
        publisher = mock(ApplicationEventPublisher.class);
        sut = new FeatureFlagService(flagRepository, publisher);
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
        RegisterFeatureFlagRequest request = RegisterFeatureFlagRequest.builder()
                .name("feature-1")
                .description("desc")
                .targetingRules(List.of())
                .build();

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

    @DisplayName("turn on feature flag when on method is called")
    @Test
    public void turnOnFeatureFlag() {
        Long id = 1L;
        FeatureFlagEntity entity = new FeatureFlagEntity(
                id,
                "flag",
                "description",
                FeatureFlagStatus.OFF,
                null,
                null,
                null,
                null
        );
        when(flagRepository.findById(id)).thenReturn(Optional.of(entity));
        FeatureFlag result = sut.on(id);
        Assertions.assertEquals(FeatureFlagStatus.ON, result.getStatus());
    }

    @DisplayName("throw exception when on method is called and flag is already on")
    @Test
    public void throwExceptionWhenOnMethodIsCalledAndFlagIsAlreadyOn() {
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
        Assertions.assertThrows(
                FeatureFlagNotUpdatedException.class, () -> sut.on(id)
        );
    }

    @DisplayName("turn off feature flag when off method is called")
    @Test
    public void turnOffFeatureFlag() {
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
        FeatureFlag result = sut.off(id);
        Assertions.assertEquals(FeatureFlagStatus.OFF, result.getStatus());
    }

    @DisplayName("throw exception when off method is called and flag is already on")
    @Test
    public void throwExceptionWhenOnMethodIsCalledAndFlagIsAlreadyOff() {
        Long id = 1L;
        FeatureFlagEntity entity = new FeatureFlagEntity(
                id,
                "flag",
                "description",
                FeatureFlagStatus.OFF,
                null,
                null,
                null,
                null
        );
        when(flagRepository.findById(id)).thenReturn(Optional.of(entity));
        Assertions.assertThrows(
                FeatureFlagNotUpdatedException.class, () -> sut.off(id)
        );
    }

    @DisplayName("archive feature flag when archive method is called")
    @Test
    public void archiveFeatureFlag() {
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
        FeatureFlag result = sut.archive(id);
        Assertions.assertNotNull(result.getArchivedAt());
    }

    @DisplayName("throw exception when archive method is called and flag is already archived")
    @Test
    public void throwExceptionWhenArchiveMethodIsCalledAndFlagIsAlreadyArchived() {
        Long id = 1L;
        FeatureFlagEntity entity = new FeatureFlagEntity(
                id,
                "flag",
                "description",
                FeatureFlagStatus.OFF,
                null,
                null,
                null,
                LocalDateTime.MAX
        );
        when(flagRepository.findById(id)).thenReturn(Optional.of(entity));
        Assertions.assertThrows(
                FeatureFlagNotUpdatedException.class, () -> sut.archive(id)
        );
    }
}