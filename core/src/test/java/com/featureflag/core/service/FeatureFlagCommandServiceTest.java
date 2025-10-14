package com.featureflag.core.service;

import com.featureflag.core.entity.FeatureFlagEntity;
import com.featureflag.core.repository.FeatureFlagRepository;
import com.featureflag.shared.api.RegisterFeatureFlagRequest;
import com.featureflag.shared.exception.FeatureFlagNotUpdatedException;
import com.featureflag.shared.model.FeatureFlag;
import com.featureflag.shared.model.FeatureFlagStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FeatureFlagCommandServiceTest {

    FeatureFlagCommandService sut;
    FeatureFlagRepository flagRepository;
    ApplicationEventPublisher publisher;

    @BeforeEach
    void setup() {
        flagRepository = mock(FeatureFlagRepository.class);
        publisher = mock(ApplicationEventPublisher.class);
        sut = new FeatureFlagCommandService(flagRepository, publisher);
    }

    @DisplayName("invoke repository save method when register feature flag")
    @Test
    public void invokeRepositorySaveMethod() {
        RegisterFeatureFlagRequest request = RegisterFeatureFlagRequest.builder()
                .name("feature-1")
                .description("desc")
                .targetingRules(List.of())
                .build();

        when(flagRepository.save(any(FeatureFlagEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FeatureFlag featureFlag = sut.register(request);

        verify(flagRepository).save(any(FeatureFlagEntity.class));
        Assertions.assertNotNull(featureFlag);
    }

    @DisplayName("turn on feature flag when on method is called")
    @Test
    public void turnOnFeatureFlag() {
        long id = 1L;
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
        long id = 1L;
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
        long id = 1L;
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
        long id = 1L;
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
        long id = 1L;
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
        long id = 1L;
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
