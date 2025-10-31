package com.featureflag.core.featureflag.application.query;

import com.featureflag.core.featureflag.domain.model.FeatureFlag;
import com.featureflag.core.featureflag.domain.repository.FeatureFlagRepository;
import com.featureflag.shared.exception.FeatureFlagNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class FeatureFlagQueryService {
    private final FeatureFlagRepository featureFlagRepository;

    @Cacheable(value = "featureFlags", key = "#flagId")
    public boolean evaluate(long flagId, Map<String, String> criteria) {
        return featureFlagRepository.findById(flagId)
                .map(flag -> flag.evaluate(criteria))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public FeatureFlag get(long id) {
        return featureFlagRepository.findById(id)
                .orElseThrow(() -> new FeatureFlagNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Page<FeatureFlag> list(Pageable pageable) {
        return featureFlagRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<FeatureFlag> findAll() {
        return featureFlagRepository.findAll();
    }

    @Transactional(readOnly = true)
    public long getLastModifiedEpochTime() {
        return featureFlagRepository.findLatest()
                .map(FeatureFlag::updatedAt)
                .map(localDateTime -> localDateTime.toEpochSecond(ZoneOffset.UTC))
                .orElse(0L);
    }
}
