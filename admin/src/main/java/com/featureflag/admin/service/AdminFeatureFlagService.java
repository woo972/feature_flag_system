package com.featureflag.admin.service;

import com.featureflag.core.service.*;
import com.featureflag.shared.model.FeatureFlag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminFeatureFlagService {
    private final FeatureFlagQueryService featureFlagQueryService;
    private final FeatureFlagCommandService featureFlagCommandService;

    public FeatureFlag get(Long id) {
        return featureFlagQueryService.get(id);
    }

    public Page<FeatureFlag> list(Pageable pageable) {
        return featureFlagQueryService.list(pageable);
    }

    public void register(RegisterFeatureFlagRequest request) {
        featureFlagCommandService.register(request);
    }
    public FeatureFlag on(Long id) {
        return featureFlagCommandService.on(id);
    }
    public FeatureFlag off(Long id) {
        return featureFlagCommandService.off(id);
    }
    public FeatureFlag archive(Long id) {
        return featureFlagCommandService.archive(id);
    }

}
