package com.featureflag.core.featureflag.domain.repository;

import com.featureflag.core.featureflag.domain.model.FeatureFlag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FeatureFlagRepository {
    FeatureFlag save(FeatureFlag featureFlag);

    Optional<FeatureFlag> findById(long id);

    List<FeatureFlag> findAll();

    Page<FeatureFlag> findAll(Pageable pageable);

    Optional<FeatureFlag> findLatest();
}
