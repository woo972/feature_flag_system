package com.featureflag.core.featureflag.application;

import com.featureflag.core.config.TestCacheConfig;
import com.featureflag.core.featureflag.application.command.FeatureFlagCommandService;
import com.featureflag.core.featureflag.application.query.FeatureFlagQueryService;
import com.featureflag.core.featureflag.domain.model.FeatureFlag;
import com.featureflag.core.featureflag.domain.repository.FeatureFlagRepository;
import com.featureflag.shared.model.FeatureFlagStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {FeatureFlagQueryService.class, FeatureFlagCommandService.class})
@Import(TestCacheConfig.class)
class FeatureFlagServiceCacheTest {
    @Autowired
    FeatureFlagQueryService featureFlagQueryService;
    @Autowired
    FeatureFlagCommandService featureFlagCommandService;
    @Autowired
    CacheManager cacheManager;

    @MockBean
    FeatureFlagRepository featureFlagRepository;
    @MockBean
    ApplicationEventPublisher publisher;

    @BeforeEach
    void setup() {
        cacheManager.getCacheNames().forEach(name -> {
            var cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        });
        when(featureFlagRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("cache hit scenario of feature flag")
    void returnsCachedFeatureFlagWhenCacheHit() {
        var featureFlag = createFlag(1L, FeatureFlagStatus.ON, null);
        warmCache(featureFlag);

        featureFlagQueryService.evaluate(featureFlag.id(), Collections.emptyMap());

        verify(featureFlagRepository, times(1)).findById(featureFlag.id());
    }

    @Nested
    @DisplayName("cache evict scenario of feature flag")
    class CacheEvict {
        @Test
        @DisplayName("refresh cache clears entries and triggers repository on subsequent lookups")
        void evictsCachedFeatureFlagOnRefresh() {
            var featureFlag = createFlag(2L, FeatureFlagStatus.ON, null);
            warmCache(featureFlag);

            featureFlagCommandService.refreshCache();
            Assertions.assertNull(cacheManager.getCache("featureFlags").get(featureFlag.id()));

            featureFlagQueryService.evaluate(featureFlag.id(), Collections.emptyMap());
            verify(featureFlagRepository, times(2)).findById(featureFlag.id());
        }

        @Test
        @DisplayName("turning flag on clears cached evaluation")
        void evictsCacheWhenFlagTurnsOn() {
            var featureFlag = createFlag(3L, FeatureFlagStatus.OFF, null);
            warmCache(featureFlag);

            featureFlagCommandService.on(featureFlag.id());
            verify(featureFlagRepository, times(2)).findById(featureFlag.id());
            Assertions.assertNull(cacheManager.getCache("featureFlags").get(featureFlag.id()));

            featureFlagQueryService.evaluate(featureFlag.id(), Collections.emptyMap());
            verify(featureFlagRepository, times(3)).findById(featureFlag.id());
        }

        @Test
        @DisplayName("turning flag off clears cached evaluation")
        void evictsCacheWhenFlagTurnsOff() {
            var featureFlag = createFlag(4L, FeatureFlagStatus.ON, null);
            warmCache(featureFlag);

            featureFlagCommandService.off(featureFlag.id());
            verify(featureFlagRepository, times(2)).findById(featureFlag.id());
            Assertions.assertNull(cacheManager.getCache("featureFlags").get(featureFlag.id()));

            featureFlagQueryService.evaluate(featureFlag.id(), Collections.emptyMap());
            verify(featureFlagRepository, times(3)).findById(featureFlag.id());
        }

        @Test
        @DisplayName("archiving flag clears cached evaluation")
        void evictsCacheWhenFlagArchived() {
            var featureFlag = createFlag(5L, FeatureFlagStatus.ON, null);
            warmCache(featureFlag);

            featureFlagCommandService.archive(featureFlag.id());
            verify(featureFlagRepository, times(2)).findById(featureFlag.id());
            Assertions.assertNull(cacheManager.getCache("featureFlags").get(featureFlag.id()));

            featureFlagQueryService.evaluate(featureFlag.id(), Collections.emptyMap());
            verify(featureFlagRepository, times(3)).findById(featureFlag.id());
        }
    }

    private void warmCache(FeatureFlag featureFlag) {
        when(featureFlagRepository.findById(featureFlag.id())).thenReturn(Optional.of(featureFlag));
        featureFlagQueryService.evaluate(featureFlag.id(), Collections.emptyMap());
    }

    private FeatureFlag createFlag(long id, FeatureFlagStatus status, LocalDateTime archivedAt) {
        var now = LocalDateTime.now();
        return new FeatureFlag(id, "feature-" + id, "description", status, Collections.emptyList(), now.minusDays(1), now, archivedAt);
    }
}
