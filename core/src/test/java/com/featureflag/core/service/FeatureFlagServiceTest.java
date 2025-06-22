package com.featureflag.core.service;

import com.featureflag.core.repository.FeatureFlagEntity;
import com.featureflag.core.repository.FeatureFlagRepository;
import com.featureflag.shared.model.FeatureFlagStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
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