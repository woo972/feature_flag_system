package com.featureflag.core.service;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFeatureFlagRequest {
    private boolean on;
    private boolean archive;
}
