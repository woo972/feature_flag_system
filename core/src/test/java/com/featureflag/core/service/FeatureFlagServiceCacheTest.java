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

import static com.featureflag.core.CacheConstants.CACHE_PREFIX;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {FeatureFlagService.class})
@Import(TestCacheConfig.class)
class FeatureFlagServiceCacheTest {
    @Autowired
    FeatureFlagService sut;
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

    @DisplayName("cacheable feature flag list")
    @Test
    public void testFindAllCache() {
        // Given
        FeatureFlagEntity testEntity = createTestFeatureFlagEntity();
        when(flagRepository.findAll()).thenReturn(List.of(testEntity));

        // When
        List<FeatureFlag> result1 = sut.findAll();
        List<FeatureFlag> result2 = sut.findAll();

        // Then
        verify(flagRepository, times(1)).findAll();
        assertNotNull(cacheManager.getCache(CACHE_PREFIX).get("all"));
        assertNotNull(result1.equals(result2));
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