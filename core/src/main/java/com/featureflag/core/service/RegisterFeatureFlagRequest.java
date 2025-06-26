package com.featureflag.core.service;

import lombok.*;
import java.util.Map;

@Getter
@Setter
@Builder
public class RegisterFeatureFlagRequest {
    private String name;
    private String description;
    private Map<String, Object> criteria;
}
