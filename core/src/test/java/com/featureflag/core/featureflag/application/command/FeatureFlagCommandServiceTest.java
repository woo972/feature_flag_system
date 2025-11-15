package com.featureflag.core.featureflag.application.command;

import com.featureflag.core.featureflag.domain.model.FeatureFlag;
import com.featureflag.core.featureflag.domain.repository.FeatureFlagRepository;
import com.featureflag.core.predefinedtargetingrule.application.PreDefinedTargetingRuleService;
import com.featureflag.core.predefinedtargetingrule.domain.model.PreDefinedTargetingRule;
import com.featureflag.shared.api.RegisterFeatureFlagRequest;
import com.featureflag.shared.api.targeting.TargetingRuleRequest;
import com.featureflag.shared.exception.FeatureFlagNotUpdatedException;
import com.featureflag.shared.model.FeatureFlagStatus;
import com.featureflag.shared.model.RuleOperator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FeatureFlagCommandServiceTest {
    private FeatureFlagCommandService featureFlagCommandService;
    private FeatureFlagRepository featureFlagRepository;
    private PreDefinedTargetingRuleService preDefinedTargetingRuleService;
    private ApplicationEventPublisher publisher;

    @BeforeEach
    void setup() {
        featureFlagRepository = mock(FeatureFlagRepository.class);
        preDefinedTargetingRuleService = mock(PreDefinedTargetingRuleService.class);
        publisher = mock(ApplicationEventPublisher.class);
        featureFlagCommandService = new FeatureFlagCommandService(
                featureFlagRepository,
                preDefinedTargetingRuleService,
                publisher);
    }

    @DisplayName("register creates and persists a feature flag")
    @Test
    void register() {
        var request = RegisterFeatureFlagRequest.builder()
                .name("flag")
                .description("description")
                .build();
        when(featureFlagRepository.save(any())).thenAnswer(invocation -> {
            var flag = invocation.getArgument(0, FeatureFlag.class);
            return new FeatureFlag(1L, flag.name(), flag.description(), flag.status(), flag.targetingRules(), flag.createdAt(), flag.updatedAt(), flag.archivedAt());
        });

        var result = featureFlagCommandService.register(request);

        verify(featureFlagRepository).save(any());
        assertNotNull(result.id());
        assertEquals(FeatureFlagStatus.OFF, result.status());
    }

    @DisplayName("turn on feature flag")
    @Test
    void turnOn() {
        var flag = existingFlag(FeatureFlagStatus.OFF, null);
        when(featureFlagRepository.findById(1L)).thenReturn(Optional.of(flag));
        when(featureFlagRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = featureFlagCommandService.on(1L);

        assertEquals(FeatureFlagStatus.ON, result.status());
    }

    @DisplayName("throw when turning on an already on flag")
    @Test
    void throwWhenTurningOnAlreadyOnFlag() {
        var flag = existingFlag(FeatureFlagStatus.ON, null);
        when(featureFlagRepository.findById(1L)).thenReturn(Optional.of(flag));

        assertThrows(FeatureFlagNotUpdatedException.class, () -> featureFlagCommandService.on(1L));
    }

    @DisplayName("turn off feature flag")
    @Test
    void turnOff() {
        var flag = existingFlag(FeatureFlagStatus.ON, null);
        when(featureFlagRepository.findById(1L)).thenReturn(Optional.of(flag));
        when(featureFlagRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = featureFlagCommandService.off(1L);

        assertEquals(FeatureFlagStatus.OFF, result.status());
    }

    @DisplayName("throw when turning off an already off flag")
    @Test
    void throwWhenTurningOffAlreadyOffFlag() {
        var flag = existingFlag(FeatureFlagStatus.OFF, null);
        when(featureFlagRepository.findById(1L)).thenReturn(Optional.of(flag));

        assertThrows(FeatureFlagNotUpdatedException.class, () -> featureFlagCommandService.off(1L));
    }

    @DisplayName("archive feature flag")
    @Test
    void archive() {
        var flag = existingFlag(FeatureFlagStatus.ON, null);
        when(featureFlagRepository.findById(1L)).thenReturn(Optional.of(flag));
        when(featureFlagRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = featureFlagCommandService.archive(1L);

        assertNotNull(result.archivedAt());
    }

    @DisplayName("throw when archiving an already archived flag")
    @Test
    void throwWhenArchivingAlreadyArchivedFlag() {
        var flag = existingFlag(FeatureFlagStatus.ON, LocalDateTime.now());
        when(featureFlagRepository.findById(1L)).thenReturn(Optional.of(flag));

        assertThrows(FeatureFlagNotUpdatedException.class, () -> featureFlagCommandService.archive(1L));
    }

    @DisplayName("register feature flag with pre-defined targeting rules")
    @Test
    void registerWithPreDefinedTargetingRules() {
        var preDefinedRule = new PreDefinedTargetingRule(
                1L,
                "country-rule",
                "Country targeting",
                RuleOperator.IN,
                List.of("US", "UK"),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(preDefinedTargetingRuleService.findById(1L)).thenReturn(preDefinedRule);
        when(featureFlagRepository.save(any())).thenAnswer(invocation -> {
            var flag = invocation.getArgument(0, FeatureFlag.class);
            return new FeatureFlag(
                    1L,
                    flag.name(),
                    flag.description(),
                    flag.status(),
                    flag.targetingRules(),
                    flag.createdAt(),
                    flag.updatedAt(),
                    flag.archivedAt()
            );
        });

        var request = RegisterFeatureFlagRequest.builder()
                .name("flag")
                .description("description")
                .preDefinedTargetingRuleIds(List.of(1L))
                .build();

        var result = featureFlagCommandService.register(request);

        assertEquals(1, result.targetingRules().size());
        assertEquals("country-rule", result.targetingRules().get(0).name());
        assertEquals(RuleOperator.IN, result.targetingRules().get(0).operator());
        verify(preDefinedTargetingRuleService).findById(1L);
        verify(featureFlagRepository).save(any());
    }

    @DisplayName("register feature flag with both inline and pre-defined targeting rules")
    @Test
    void registerWithBothInlineAndPreDefinedRules() {
        var preDefinedRule = new PreDefinedTargetingRule(
                1L,
                "country-rule",
                "Country targeting",
                RuleOperator.IN,
                List.of("US", "UK"),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        var inlineRule = TargetingRuleRequest.builder()
                .name("user-type")
                .operator(RuleOperator.EQUAL)
                .values(List.of("premium"))
                .build();

        when(preDefinedTargetingRuleService.findById(1L)).thenReturn(preDefinedRule);
        when(featureFlagRepository.save(any())).thenAnswer(invocation -> {
            var flag = invocation.getArgument(0, FeatureFlag.class);
            return new FeatureFlag(
                    1L,
                    flag.name(),
                    flag.description(),
                    flag.status(),
                    flag.targetingRules(),
                    flag.createdAt(),
                    flag.updatedAt(),
                    flag.archivedAt()
            );
        });

        var request = RegisterFeatureFlagRequest.builder()
                .name("flag")
                .description("description")
                .targetingRules(List.of(inlineRule))
                .preDefinedTargetingRuleIds(List.of(1L))
                .build();

        var result = featureFlagCommandService.register(request);

        assertEquals(2, result.targetingRules().size());
        assertEquals("user-type", result.targetingRules().get(0).name());
        assertEquals("country-rule", result.targetingRules().get(1).name());
        verify(preDefinedTargetingRuleService).findById(1L);
        verify(featureFlagRepository).save(any());
    }

    private FeatureFlag existingFlag(FeatureFlagStatus status, LocalDateTime archivedAt) {
        var now = LocalDateTime.now();
        return new FeatureFlag(1L, "flag", "description", status, List.of(), now.minusDays(1), now, archivedAt);
    }
}
