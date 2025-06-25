package com.featureflag.core.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class RegisterFeatureFlagRequest {
    private String name;
    private String description;
    private Map<String, Object> criteria;
}
