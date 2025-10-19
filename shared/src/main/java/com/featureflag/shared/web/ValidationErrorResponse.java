package com.featureflag.shared.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponse {
    private Instant timestamp;
    private String message;
    private List<Violation> violations;
}
