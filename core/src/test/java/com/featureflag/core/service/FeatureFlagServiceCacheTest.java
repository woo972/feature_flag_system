package com.featureflag.core.service;

import com.featureflag.core.config.*;
import com.featureflag.core.entity.*;
import com.featureflag.core.repository.*;
import com.featureflag.shared.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.cache.*;
import org.springframework.context.*;
import org.springframework.context.annotation.*;
import java.time.*;
import java.util.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {FeatureFlagQueryService.class, FeatureFlagCommandService.class})
@Import(TestCacheConfig.class)
class FeatureFlagServiceCacheTest {
    @Autowired
    FeatureFlagQueryService sut;
    @Autowired
    FeatureFlagCommandService commandService;
    @Autowired
    CacheManager cacheManager;

    @MockBean
    FeatureFlagRepository flagRepository;
    @MockBean
    ApplicationEventPublisher publisher;


    @BeforeEach
    void setup() {
        cacheManager.getCacheNames().forEach(name -> cacheManager.getCache(name).clear());
    }

    @Test
    @DisplayName("cache hit scenario of feature flag")
    public void returnsCachedFeatureFlagWhenCacheHit() {
        var testFeatureFlagEntity = createTestFeatureFlagEntity();
        warmCache(testFeatureFlagEntity);

        sut.evaluate(testFeatureFlagEntity.getId(), Collections.emptyMap());
        verify(flagRepository, times(1)).findById(testFeatureFlagEntity.getId());
    }

    @Nested
    @DisplayName("cache evict scenario of feature flag")
    class CacheEvict {
        @Test
        @DisplayName("refresh cache clears entries and triggers repository on subsequent lookups")
        void evictsCachedFeatureFlagOnRefresh() {
            var featureFlag = createTestFeatureFlagEntity();
            warmCache(featureFlag);

            commandService.refreshCache();
            Assertions.assertNull(cacheManager.getCache("featureFlags").get(featureFlag.getId()));

            sut.evaluate(featureFlag.getId(), Collections.emptyMap());
            verify(flagRepository, times(2)).findById(featureFlag.getId());
        }

        @Test
        @DisplayName("turning flag on clears cached evaluation")
        void evictsCacheWhenFlagTurnsOn() {
            var featureFlag = createOffFeatureFlagEntity();
            warmCache(featureFlag);

            commandService.on(featureFlag.getId());
            verify(flagRepository, times(2)).findById(featureFlag.getId());
            Assertions.assertNull(cacheManager.getCache("featureFlags").get(featureFlag.getId()));

            sut.evaluate(featureFlag.getId(), Collections.emptyMap());
            verify(flagRepository, times(3)).findById(featureFlag.getId());
        }

        @Test
        @DisplayName("turning flag off clears cached evaluation")
        void evictsCacheWhenFlagTurnsOff() {
            var featureFlag = createTestFeatureFlagEntity();
            warmCache(featureFlag);

            commandService.off(featureFlag.getId());
            verify(flagRepository, times(2)).findById(featureFlag.getId());
            Assertions.assertNull(cacheManager.getCache("featureFlags").get(featureFlag.getId()));

            sut.evaluate(featureFlag.getId(), Collections.emptyMap());
            verify(flagRepository, times(3)).findById(featureFlag.getId());
        }

        @Test
        @DisplayName("archiving flag clears cached evaluation")
        void evictsCacheWhenFlagArchived() {
            var featureFlag = createArchivableFeatureFlagEntity();
            warmCache(featureFlag);

            commandService.archive(featureFlag.getId());
            verify(flagRepository, times(2)).findById(featureFlag.getId());
            Assertions.assertNull(cacheManager.getCache("featureFlags").get(featureFlag.getId()));

            sut.evaluate(featureFlag.getId(), Collections.emptyMap());
            verify(flagRepository, times(3)).findById(featureFlag.getId());
        }
    }

    private void warmCache(FeatureFlagEntity featureFlag) {
        when(flagRepository.findById(featureFlag.getId())).thenReturn(Optional.of(featureFlag));
        sut.evaluate(featureFlag.getId(), Collections.emptyMap());
    }


    private static FeatureFlagEntity createTestFeatureFlagEntity() {
        return createFeatureFlagEntity(1L, "test-flag", FeatureFlagStatus.ON, null);
    }

    private static FeatureFlagEntity createOffFeatureFlagEntity() {
        return createFeatureFlagEntity(2L, "test-flag-off", FeatureFlagStatus.OFF, null);
    }

    private static FeatureFlagEntity createArchivableFeatureFlagEntity() {
        return createFeatureFlagEntity(3L, "test-flag-archivable", FeatureFlagStatus.ON, null);
    }

    private static FeatureFlagEntity createFeatureFlagEntity(long id, String name, FeatureFlagStatus status, LocalDateTime archivedAt) {
        return new FeatureFlagEntity(
                id,
                name,
                "Test description",
                status,
                null,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now(),
                archivedAt
        );
    }
}
