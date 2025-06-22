package com.featureflag.core.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class RegisterFeatureFlagRequest {
    private String name;
    private String description;
    private Map<String, Object> criteria;
}
