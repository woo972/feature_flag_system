package com.featureflag.admin.service;

import com.featureflag.core.service.FeatureFlagService;
import com.featureflag.core.service.RegisterFeatureFlagRequest;
import com.featureflag.core.service.UpdateFeatureFlagRequest;
import com.featureflag.shared.model.FeatureFlag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminFeatureFlagService {
    private final FeatureFlagService featureFlagService;

    public FeatureFlag get(Long id) {
        return featureFlagService.get(id);
    }

    public Page<FeatureFlag> list(Pageable pageable) {
        return featureFlagService.list(pageable);
    }

    public void register(RegisterFeatureFlagRequest request) {
        featureFlagService.register(request);
    }

    public FeatureFlag update(Long id, UpdateFeatureFlagRequest request) {
        return featureFlagService.update(id, request);
    }
}
