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

@SpringBootTest(classes = {FeatureFlagQueryService.class})
@Import(TestCacheConfig.class)
class FeatureFlagQueryServiceCacheTest {
    @Autowired
    FeatureFlagQueryService sut;
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
        when(flagRepository.findById(testFeatureFlagEntity.getId())).thenReturn(Optional.of(testFeatureFlagEntity));
        sut.evaluate(testFeatureFlagEntity.getId(), Collections.emptyMap());
        sut.evaluate(testFeatureFlagEntity.getId(), Collections.emptyMap());
        verify(flagRepository, times(1)).findById(testFeatureFlagEntity.getId());
    }

    @Nested
    @DisplayName("cache evict scenario of feature flag")
    class CacheEvict {
    }


    private static FeatureFlagEntity createTestFeatureFlagEntity() {
        return new FeatureFlagEntity(
                1L,
                "test-flag",
                "Test description",
                FeatureFlagStatus.ON,
                null,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now(),
                null
        );
    }
}