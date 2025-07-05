package com.featureflag.core.service;

import com.featureflag.shared.model.TargetingRule;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFeatureFlagRequest {
    private boolean on;
    private boolean archive;
}
